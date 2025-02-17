package com.Gen2Play.AuthorizationService.repository.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Gen2Play.AuthorizationService.model.entity.Account;

@Repository
public interface IAccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByEmail(String email);
    Optional<List<Account>> findAllByRoleRoleId(Long roleid);
}