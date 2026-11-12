package com.Recur.subscription_tracker.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Recur.subscription_tracker.model.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    List<Subscription> findByUserIdOrderByRenewsOnAsc(UUID userId);

    Optional<Subscription> findByIdAndUserId(UUID id, UUID userId);

}
