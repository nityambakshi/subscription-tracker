package com.Recur.subscription_tracker.contoller;

import java.util.Map;

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
import com.Recur.subscription_tracker.dto.RefreshRequestDTO;
import com.Recur.subscription_tracker.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> me(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(Map.of("username", authentication.getName()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @RequestBody AuthRequestDTO authRequestDTO,
            @RequestHeader(value = "X-Device-Name", required = false) String device,
            HttpServletResponse response) {
        AuthResponseDTO authResponseDTO = authService.login(authRequestDTO, device);
        authService.setAuthCookie(response, authResponseDTO);
        return ResponseEntity.ok(authResponseDTO);
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDTO> register(
            @RequestBody AuthRequestDTO authRequestDTO,
            @RequestHeader(value = "X-Device-Name", required = false) String device,
            HttpServletResponse response) {
        AuthResponseDTO register = authService.register(authRequestDTO, device);
        authService.setAuthCookie(response, register);
        return ResponseEntity.ok(register);
    }

    @PostMapping("/refresh")
    public AuthResponseDTO refreshToken(@RequestBody RefreshRequestDTO refreshRequestDTO) {
        return authService.refresh(refreshRequestDTO.getRefreshToken());
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
