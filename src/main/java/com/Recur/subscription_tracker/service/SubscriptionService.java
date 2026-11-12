package com.Recur.subscription_tracker.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.Recur.subscription_tracker.dto.SubscriptionDTO;
import com.Recur.subscription_tracker.model.Subscription;
import com.Recur.subscription_tracker.model.User;
import com.Recur.subscription_tracker.repository.SubscriptionRepository;
import com.Recur.subscription_tracker.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    private final UserRepository userRepository;

    public List<SubscriptionDTO> listUserSubscriptions(UUID userId) {
        return subscriptionRepository.findByUserIdOrderByRenewsOnAsc(userId)
                .stream()
                .map(this::modelToDto)
                .toList();
    }

    public void createNewSubscription(SubscriptionDTO subscriptionDTO, UUID userId) {
        User user = userRepository.getReferenceById(userId);

        Subscription subscription = new Subscription();
        subscription.setAmount(subscriptionDTO.amount());
        subscription.setRenewsOn(subscriptionDTO.renewsOn());
        subscription.setReminderOn(subscriptionDTO.reminderOn());
        subscription.setCategory(subscriptionDTO.category());
        subscription.setCurrency(subscriptionDTO.currency());
        subscription.setCycle(subscriptionDTO.cycle());
        subscription.setName(subscriptionDTO.name());
        subscription.setLogoKey(subscriptionDTO.logoKey());
        subscription.setUser(user);
        subscriptionRepository.save(subscription);
    }

    private SubscriptionDTO modelToDto(Subscription subscription) {
        return new SubscriptionDTO(subscription.getId(), subscription.getName(), subscription.getLogoKey(),
                subscription.getCategory(), subscription.getAmount(), subscription.getCurrency(),
                subscription.getCycle(), subscription.getRenewsOn(), subscription.isReminderOn());
    }
}
