package com.Gen2Play.AuthorizationService.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RolePermissionRequestDTO {
    private Long roleId;
    private Long permissionId;
    private boolean status;
}
