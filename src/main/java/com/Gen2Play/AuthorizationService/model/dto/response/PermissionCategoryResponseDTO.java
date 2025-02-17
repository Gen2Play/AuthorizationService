package com.Gen2Play.AuthorizationService.model.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionCategoryResponseDTO {
    private Long permissionCategoryId;
    private String permissionCategoryName;
    private boolean status;
    List<PermissionListResponseDTO> permissions;
}
