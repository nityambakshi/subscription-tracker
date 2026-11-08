package com.Recur.subscription_tracker.repository;

import com.Recur.subscription_tracker.model.Session;
import com.Recur.subscription_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {

    Optional<Session> findByRefreshTokenHash(String hash);

    Optional<Session> findByPreviousRefreshTokenHash(String hash);

    Optional<Session> findByIdAndUser(UUID id, User user);

    List<Session> findAllByUserAndRevokedFalse(User user);

    void deleteByExpiresAtBefore(Instant cutoff);
}
