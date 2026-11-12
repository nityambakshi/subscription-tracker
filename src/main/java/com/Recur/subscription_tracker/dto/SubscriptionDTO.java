package com.Recur.subscription_tracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.Recur.subscription_tracker.enums.BillingCycle;

public record SubscriptionDTO(
        UUID id,
        String name,
        String logoKey,
        String category,
        BigDecimal amount,
        String currency,
        BillingCycle cycle,
        LocalDate renewsOn,
        boolean reminderOn) {
}