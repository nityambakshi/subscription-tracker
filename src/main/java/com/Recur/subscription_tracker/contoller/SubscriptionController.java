package com.Recur.subscription_tracker.contoller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Recur.subscription_tracker.configuration.AuthenticatedUserPrincipal;
import com.Recur.subscription_tracker.dto.SubscriptionDTO;
import com.Recur.subscription_tracker.service.SubscriptionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping
    public ResponseEntity<List<SubscriptionDTO>> getAllSubscriptions(
            @AuthenticationPrincipal AuthenticatedUserPrincipal user) {
        return ResponseEntity.ok(subscriptionService.listUserSubscriptions(user.getUserId()));
    }

    @PostMapping
    public ResponseEntity<Void> createSubscription(@RequestBody SubscriptionDTO subscriptionDTO,
            @AuthenticationPrincipal AuthenticatedUserPrincipal user) {
        subscriptionService.createNewSubscription(subscriptionDTO, user.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}