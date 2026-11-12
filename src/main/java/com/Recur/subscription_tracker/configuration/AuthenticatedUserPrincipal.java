package com.Recur.subscription_tracker.configuration;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.Recur.subscription_tracker.enums.Role;
import com.Recur.subscription_tracker.model.User;

public class AuthenticatedUserPrincipal implements UserDetails {

    private UUID userId;
    private final String username;
    private final String password;
    private final List<GrantedAuthority> authorities;

    public AuthenticatedUserPrincipal(User user) {
        userId = user.getId();
        username = user.getUsername();
        password = user.getPasswordHash();
        authorities = List.of(new SimpleGrantedAuthority(Role.USER.name()));
    }

    public @NonNull UUID getUserId() {
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public @NonNull String getUsername() {
        return username;
    }
}
