package com.Recur.subscription_tracker.dto;

import com.Recur.subscription_tracker.model.User;
import lombok.Builder;

import java.util.UUID;

@Builder
public class AuthResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String username;
    private UUID userId;
    private User.Role role = User.Role.USER;
}
