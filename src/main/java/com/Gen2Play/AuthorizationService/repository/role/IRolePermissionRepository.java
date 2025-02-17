package com.Gen2Play.AuthorizationService.repository.role;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Gen2Play.AuthorizationService.model.entity.Role_Permission;

@Repository
public interface IRolePermissionRepository extends JpaRepository<Role_Permission, Long> {
    List<Role_Permission> findAllByRole_RoleId(Long roleId);
    List<Role_Permission> findAllByPermission_PermissionId(Long permissionId);
    Optional<Role_Permission> findByRole_RoleIdAndPermission_PermissionId(Long roleId, Long permissionId);
}
