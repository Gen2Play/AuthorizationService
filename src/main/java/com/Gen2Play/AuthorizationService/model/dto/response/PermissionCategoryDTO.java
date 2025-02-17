package com.Gen2Play.AuthorizationService.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionCategoryDTO {
    private Long id;
    private String name;
    private boolean status;
}
