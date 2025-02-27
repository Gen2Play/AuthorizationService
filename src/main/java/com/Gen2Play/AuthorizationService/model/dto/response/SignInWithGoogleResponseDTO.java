package com.Gen2Play.AuthorizationService.model.dto.response;

import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInWithGoogleResponseDTO {
    private String jwtToken;
    private String refreshToken;
    @NotNull
    @Email
    private String email;
    private String name;
    private UUID id;
    private String role;
    private Set<String> permissions;
    private long exp;
}
