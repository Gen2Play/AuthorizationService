package com.Gen2Play.AuthorizationService.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequestDTO {
    private Long roleId;
    private String roleName;
    private String roleCode;
    private String description;
}
