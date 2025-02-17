package com.Gen2Play.AuthorizationService.model.dto.request;

import java.util.UUID;

import lombok.Data;

@Data
public class AccountRequestDTO {
    private UUID accountId;
    private String name;
    private String email;
    private Long roleId;
    private String avatar;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
