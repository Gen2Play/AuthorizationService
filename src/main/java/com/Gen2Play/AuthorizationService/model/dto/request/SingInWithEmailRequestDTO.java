package com.Gen2Play.AuthorizationService.model.dto.request;

import lombok.Data;

@Data
public class SingInWithEmailRequestDTO {
    private String email;
    private String password;
}
