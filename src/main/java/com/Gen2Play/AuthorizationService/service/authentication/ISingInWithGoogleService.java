package com.Gen2Play.AuthorizationService.service.authentication;

import com.Gen2Play.AuthorizationService.model.dto.response.SignInWithGoogleResponseDTO;

public interface  ISingInWithGoogleService {
    SignInWithGoogleResponseDTO authenticateWithGoogle(String email);
    SignInWithGoogleResponseDTO authenticateWithEmail(String email, String password);
}
