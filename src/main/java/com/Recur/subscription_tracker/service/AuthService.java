package com.Recur.subscription_tracker.service;

import com.Recur.subscription_tracker.dto.AuthRequestDTO;
import com.Recur.subscription_tracker.dto.AuthResponseDTO;
import com.Recur.subscription_tracker.model.Session;
import com.Recur.subscription_tracker.model.User;
import com.Recur.subscription_tracker.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final SessionService sessionService;

    private final JwtUtil jwtUtil;

    public AuthResponseDTO login(AuthRequestDTO authRequestDTO, String deviceId) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequestDTO.getEmail(), authRequestDTO.getPassword())
        );

        return issueToken(userService.getUserByUserName(authRequestDTO.getEmail()), deviceId);
    }

    public AuthResponseDTO register(AuthRequestDTO authRequestDTO,  String deviceId) {
        userService.createUser(authRequestDTO.getEmail(), authRequestDTO.getPassword());
        return issueToken(userService.getUserByUserName(authRequestDTO.getEmail()), deviceId);
    }

    public AuthResponseDTO refresh(String rawRefreshToken) {
        CreatedSession next = sessionService.rotate(rawRefreshToken);
        return getTokenFor(next);
    }

    public void logout(String rawRefreshToken) {
        sessionService.revoke(rawRefreshToken);
    }

    private AuthResponseDTO issueToken(User user, String deviceId) {
        CreatedSession createdSession = sessionService.create(user, deviceId);

        return getTokenFor(createdSession);
    }

    private AuthResponseDTO getTokenFor(CreatedSession createdSession) {
        Session session = createdSession.session();
        String username = session.getUser().getUsername();

        String accessToken = jwtUtil.generateJwtToken(username, session.getId());
        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .userId(session.getUser().getId())
                .username(username)
                .refreshToken(createdSession.rawRefreshToken())
                .build();
    }

}
