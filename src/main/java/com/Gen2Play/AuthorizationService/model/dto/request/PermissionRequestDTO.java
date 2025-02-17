package com.Gen2Play.AuthorizationService.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionRequestDTO {
    private Long permissionId;
    private String permissionName;
    private String description;
    private Long permissionCategoryId;
    private String action;
}
