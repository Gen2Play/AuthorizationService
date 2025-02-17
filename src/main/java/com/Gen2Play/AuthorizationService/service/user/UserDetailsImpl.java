package com.Gen2Play.AuthorizationService.service.user;

import java.util.Set;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class UserDetailsImpl extends User {
    private final UUID accountId;

    public UserDetailsImpl(UUID accountId, String email, Set<GrantedAuthority> authorities) {
        super(email, "", authorities);
        this.accountId = accountId;
    }

    public UUID getAccountId() {
        return accountId;
    }
}
