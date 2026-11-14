package com.Recur.subscription_tracker.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.Recur.subscription_tracker.dto.AuthRequestDTO;
import com.Recur.subscription_tracker.dto.AuthResponseDTO;
import com.Recur.subscription_tracker.dto.CreatedSession;
import com.Recur.subscription_tracker.model.Session;
import com.Recur.subscription_tracker.model.User;
import com.Recur.subscription_tracker.utils.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final SessionService sessionService;

    private final JwtUtil jwtUtil;

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    public AuthResponseDTO login(AuthRequestDTO authRequestDTO, String deviceId, HttpServletResponse response) {
        String normalizedEmail = authRequestDTO.getEmail().trim().toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, authRequestDTO.getPassword()));

        User user = userService.getUserByUserName(normalizedEmail);
        return startSession(sessionService.create(user, deviceId), response);
    }

    public AuthResponseDTO register(AuthRequestDTO authRequestDTO, String deviceId, HttpServletResponse response) {
        String email = authRequestDTO.getEmail().trim().toLowerCase();

        userService.createUser(authRequestDTO.getFullName(), email, authRequestDTO.getPassword());
        User user = userService.getUserByUserName(email);

        return startSession(sessionService.create(user, deviceId), response);
    }

    public AuthResponseDTO refresh(String rawRefreshToken, HttpServletResponse response) {
        return startSession(sessionService.rotate(rawRefreshToken), response);
    }

    public void logout(String rawRefreshToken) {
        sessionService.revoke(rawRefreshToken);
    }

    private AuthResponseDTO startSession(CreatedSession created, HttpServletResponse response) {
        Session session = created.session();
        User user = session.getUser();

        String accessToken = jwtUtil.generateJwtToken(user.getUsername(), session.getId());
        setAuthCookie(response, accessToken, created.rawRefreshToken());

        return new AuthResponseDTO(user.getUsername(), user.getId(), user.getFullName());
    }

    private void setAuthCookie(HttpServletResponse response, String accessToken, String refreshToken) {
        ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/api/v1/auth")
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    public void clearAuthCookies(HttpServletResponse response) {
        ResponseCookie accessCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/api/v1/auth")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}
