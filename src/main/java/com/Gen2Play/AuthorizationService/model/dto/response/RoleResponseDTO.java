package com.Gen2Play.AuthorizationService.model.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponseDTO {
    private Long roleId;
    private String roleName;
    private String roleCode;
    private String description;
    private boolean status;
    private LocalDateTime lastUpdatedAt;
    private int totalUser;
}
