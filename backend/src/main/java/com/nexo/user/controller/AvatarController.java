package com.nexo.user.controller;

import com.nexo.user.dto.UserView;
import com.nexo.user.service.AvatarService;
import com.nexo.user.service.CurrentUserService;
import java.util.UUID;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class AvatarController {
    private final AvatarService avatars;
    private final CurrentUserService currentUser;

    public AvatarController(AvatarService avatars, CurrentUserService currentUser) {
        this.avatars = avatars;
        this.currentUser = currentUser;
    }

    @PostMapping(value = "/users/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
    public UserView upload(Authentication authentication, @RequestParam("file") MultipartFile file) {
        return avatars.upload(currentUser.require(authentication).userId(), file);
    }

    @DeleteMapping("/users/me/avatar")
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
    public UserView remove(Authentication authentication) {
        return avatars.remove(currentUser.require(authentication).userId());
    }

    @GetMapping(value = "/avatars/{userId}/{version}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> read(@PathVariable UUID userId, @PathVariable UUID version) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .contentType(MediaType.IMAGE_PNG).body(avatars.read(userId, version));
    }
}
