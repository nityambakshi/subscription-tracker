package com.Recur.subscription_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshRequestDTO {

    @NotBlank
    private String refreshToken;
}

