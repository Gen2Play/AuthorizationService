package com.Gen2Play.AuthorizationService.service.role;

import com.Gen2Play.AuthorizationService.model.dto.request.RolePermissionRequestDTO;
import com.Gen2Play.AuthorizationService.model.dto.response.OperationStatus;
import com.Gen2Play.AuthorizationService.model.dto.response.RolePermissionResponseDTO;
import com.Gen2Play.AuthorizationService.model.entity.Permission;
import com.Gen2Play.AuthorizationService.model.entity.Role;

public interface IRolePermissionService {
    RolePermissionResponseDTO getRolePermissionById(Long id);
    OperationStatus createRolePermission(Permission permission, Role role);
    OperationStatus updateRolePermission(RolePermissionRequestDTO rolePermissionRequestDTO);
    // OperationStatus deleteRolePermission(Long id);
}
