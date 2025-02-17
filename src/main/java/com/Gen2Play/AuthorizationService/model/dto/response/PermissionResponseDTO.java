package com.Gen2Play.AuthorizationService.model.dto.response;

import com.Gen2Play.AuthorizationService.model.entity.Permission_Category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionResponseDTO {
    private Long permissionId;
    private String permissionName;
    private String description;
    private String action;
    private boolean status;
    private Permission_Category permissionCategory;
}
    