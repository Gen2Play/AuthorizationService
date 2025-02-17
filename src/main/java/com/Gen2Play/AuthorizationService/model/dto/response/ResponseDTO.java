package com.Gen2Play.AuthorizationService.model.dto.response;

import org.springframework.http.HttpStatusCode;
import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO<T> {
    @Nullable
    private T data;
    private String message;
    private HttpStatusCode status;
}