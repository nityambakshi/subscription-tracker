package com.Recur.subscription_tracker.contoller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Recur.subscription_tracker.dto.AuthRequestDTO;
import com.Recur.subscription_tracker.dto.AuthResponseDTO;
import com.Recur.subscription_tracker.dto.RefreshRequestDTO;
import com.Recur.subscription_tracker.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @RequestBody AuthRequestDTO authRequestDTO,
            @RequestHeader(value = "X-Device-Name", required = false) String device) {
        return ResponseEntity.ok(authService.login(authRequestDTO, device));
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDTO> register(
            @RequestBody AuthRequestDTO authRequestDTO,
            @RequestHeader(value = "X-Device-Name", required = false) String device) {
        return ResponseEntity.ok(authService.register(authRequestDTO, device));
    }

    @PostMapping("/refresh")
    public AuthResponseDTO refreshToken(@RequestBody RefreshRequestDTO refreshRequestDTO) {
        return authService.refresh(refreshRequestDTO.getRefreshToken());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequestDTO req) {
        authService.logout(req.getRefreshToken());
        return ResponseEntity.noContent().build();
    }
}
