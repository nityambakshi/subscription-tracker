package com.Recur.subscription_tracker.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SessionDTO {
    private UUID id;
    private String deviceName;
    private Instant createdAt;
    private Instant lastUsedAt;
}
