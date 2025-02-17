package com.Gen2Play.AuthorizationService.service.permission;

import java.util.List;

import com.Gen2Play.AuthorizationService.model.dto.request.PermissionRequestDTO;
import com.Gen2Play.AuthorizationService.model.dto.response.OperationStatus;
import com.Gen2Play.AuthorizationService.model.dto.response.PermissionCategoryResponseDTO;
import com.Gen2Play.AuthorizationService.model.dto.response.PermissionResponseDTO;

public interface IPermissionService {
    List<PermissionResponseDTO> getAllPermissions();
    PermissionResponseDTO getPermissionById(Long permissionId);
    OperationStatus createPermission(PermissionRequestDTO permissionRequestDTO);
    OperationStatus updatePermission(PermissionRequestDTO permissionRequestDTO);
    OperationStatus deletePermission(Long permissionId);
    List<PermissionCategoryResponseDTO> getAllPermissionByPermissionCategory();
}
