package com.Recur.subscription_tracker.dto;

import com.Recur.subscription_tracker.model.Session;

public record CreatedSession(Session session, String rawRefreshToken) {
}