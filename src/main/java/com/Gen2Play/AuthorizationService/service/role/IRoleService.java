package com.Gen2Play.AuthorizationService.service.role;

import java.util.List;

import com.Gen2Play.AuthorizationService.model.dto.request.RoleRequestDTO;
import com.Gen2Play.AuthorizationService.model.dto.response.OperationStatus;
import com.Gen2Play.AuthorizationService.model.dto.response.RoleResponseDTO;

public interface IRoleService {
    List<RoleResponseDTO> getAllRoles();
    RoleResponseDTO getRoleById(Long roleId);
    RoleResponseDTO getRoleByName(String roleName);
    OperationStatus createRole(RoleRequestDTO roleRequestDTO);
    OperationStatus updateRole(RoleRequestDTO roleRequestDTO);
    OperationStatus deleteRole(Long roleId);
    List<RoleResponseDTO> getAllRolesByRole();
}
