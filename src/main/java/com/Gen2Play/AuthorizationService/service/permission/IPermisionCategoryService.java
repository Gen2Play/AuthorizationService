package com.Gen2Play.AuthorizationService.service.permission;

import java.util.List;
import java.util.Optional;

import com.Gen2Play.AuthorizationService.model.dto.request.PermissionCategoryRequestDTO;
import com.Gen2Play.AuthorizationService.model.dto.response.OperationStatus;
import com.Gen2Play.AuthorizationService.model.dto.response.PermissionCategoryDTO;

public interface  IPermisionCategoryService {
    List<PermissionCategoryDTO> getAllPermissionCategory();
    Optional<PermissionCategoryDTO> getPermissionCategoryById(Long id);
    Optional<PermissionCategoryDTO> getPermissionCategoryByName(String name);
    OperationStatus createPermissionCategory(PermissionCategoryRequestDTO permissionCategoryDTO);
    OperationStatus updatePermissionCategory(PermissionCategoryRequestDTO permissionCategoryDTO);
    OperationStatus deletePermissionCategory(Long id);
}
