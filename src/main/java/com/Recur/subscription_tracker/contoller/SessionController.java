package com.Recur.subscription_tracker.contoller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Recur.subscription_tracker.dto.SessionDTO;
import com.Recur.subscription_tracker.model.User;
import com.Recur.subscription_tracker.service.SessionService;
import com.Recur.subscription_tracker.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final UserService userService;

    @GetMapping
    public List<SessionDTO> list(Authentication auth) {
        User user = userService.getUserByUserName(auth.getName());
        return sessionService.activeSessions(user).stream()
                .map(s -> new SessionDTO(s.getId(), s.getDeviceName(), s.getCreatedAt(), s.getLastUsedAt()))
                .toList();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> revoke(@PathVariable UUID id, Authentication auth) {
        sessionService.revokeOne(id, userService.getUserByUserName(auth.getName()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/revoke-all")
    public ResponseEntity<Void> revokeAll(Authentication auth) {
        sessionService.revokeAll(userService.getUserByUserName(auth.getName()));
        return ResponseEntity.noContent().build();
    }
}
