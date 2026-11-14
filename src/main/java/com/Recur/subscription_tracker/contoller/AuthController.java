package com.Recur.subscription_tracker.contoller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Recur.subscription_tracker.dto.AuthRequestDTO;
import com.Recur.subscription_tracker.dto.AuthResponseDTO;
import com.Recur.subscription_tracker.model.User;
import com.Recur.subscription_tracker.service.AuthService;
import com.Recur.subscription_tracker.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<AuthResponseDTO> me(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.getUserByUserName(authentication.getName());
        return ResponseEntity.ok(new AuthResponseDTO(user.getUsername(), user.getId(), user.getFullName()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @RequestBody AuthRequestDTO authRequestDTO,
            @RequestHeader(value = "X-Device-Name", required = false) String device,
            HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(authRequestDTO, device, response));
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDTO> register(
            @RequestBody AuthRequestDTO authRequestDTO,
            @RequestHeader(value = "X-Device-Name", required = false) String device,
            HttpServletResponse response) {
        return ResponseEntity.ok(authService.register(authRequestDTO, device, response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(authService.refresh(refreshToken, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) {
        if (refreshToken != null) {
            authService.logout(refreshToken);
        }
        authService.clearAuthCookies(response);
        return ResponseEntity.noContent().build();
    }
}
