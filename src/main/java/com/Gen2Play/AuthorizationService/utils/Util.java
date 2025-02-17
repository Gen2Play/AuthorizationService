package com.Gen2Play.AuthorizationService.utils;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.Gen2Play.AuthorizationService.model.entity.Account;
import com.Gen2Play.AuthorizationService.repository.user.IAccountRepository;
import com.Gen2Play.AuthorizationService.service.user.UserDetailsImpl;

@Component
public class Util {
    @Autowired
    private IAccountRepository accountRepository;

    // Get the account id of the authenticated user
    public static UUID getAuthenticatedAccountId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            return userDetails.getAccountId();
        }
        return null;
    }

    public Account getAuthenticatedAccount() {
        try {
            UUID accountId = getAuthenticatedAccountId();
            if (accountId != null) {
                return accountRepository.findById(accountId).get();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    // Get the current date and time
    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
}
