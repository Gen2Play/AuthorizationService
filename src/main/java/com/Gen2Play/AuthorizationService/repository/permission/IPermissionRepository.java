package com.Gen2Play.AuthorizationService.repository.permission;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Gen2Play.AuthorizationService.model.entity.Permission;
import com.Gen2Play.AuthorizationService.model.entity.Permission_Category;

@Repository
public interface  IPermissionRepository extends JpaRepository<Permission, Long>{
    Optional<Permission> findByPermissionName(String name);
    Optional<Permission> findByAction(String action);
    Optional<List<Permission>> findAllByPermissionCategory(Permission_Category permissionCategory);
}
