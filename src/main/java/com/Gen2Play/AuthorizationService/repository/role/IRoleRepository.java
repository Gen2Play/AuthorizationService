package com.Gen2Play.AuthorizationService.repository.role;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Gen2Play.AuthorizationService.model.entity.Role;

@Repository
public interface IRoleRepository extends JpaRepository<Role, Long>{
    Optional<Role> findByRoleName(String name);
    Optional<Role> findByRoleCode(String code);
}
