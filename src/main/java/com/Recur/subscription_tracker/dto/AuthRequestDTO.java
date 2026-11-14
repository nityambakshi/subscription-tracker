package com.Recur.subscription_tracker.dto;

import lombok.Data;

@Data
public class AuthRequestDTO {
    private String fullName;
    private String email;
    private String password;
}
