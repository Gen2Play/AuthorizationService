package com.Gen2Play.AuthorizationService.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Gen2Play.AuthorizationService.model.dto.request.SingInWithEmailRequestDTO;
import com.Gen2Play.AuthorizationService.model.dto.response.ResponseDTO;
import com.Gen2Play.AuthorizationService.model.dto.response.SignInWithGoogleResponseDTO;
import com.Gen2Play.AuthorizationService.model.dto.response.TokenResponseDTO;
import com.Gen2Play.AuthorizationService.service.authentication.ISingInWithGoogleService;
import com.Gen2Play.AuthorizationService.service.authentication.VerificationResponse;
import com.Gen2Play.AuthorizationService.service.authentication.VerificationService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final ISingInWithGoogleService singInWithGoogleService;
    private final VerificationService verificationService;
    private final String INVALID_EMAIL = "Email not found";
    private final String SUCCESS = "Authentication successfully";
    private final String NOT_FOUND = "Not found";
    private final String ERROR = "An error occurred";

    public AuthController(ISingInWithGoogleService singInWithGoogleService, VerificationService verificationService) {
        this.singInWithGoogleService = singInWithGoogleService;
        this.verificationService = verificationService;
    }

    @PostMapping("/signingGoogle")
    public ResponseDTO<?> signInWithGoogle(@RequestBody String email) {
        ResponseDTO<SignInWithGoogleResponseDTO> responseDTO = new ResponseDTO<>();
        try {
            if (email == null) {
                responseDTO.setMessage(INVALID_EMAIL);
                responseDTO.setStatus(HttpStatus.BAD_REQUEST);
            }
            SignInWithGoogleResponseDTO response = singInWithGoogleService.authenticateWithGoogle(email);
            responseDTO.setData(response);
            responseDTO.setStatus(HttpStatus.OK);
            responseDTO.setMessage(SUCCESS);
        } catch (IllegalStateException e) {
            responseDTO.setMessage(NOT_FOUND);
            responseDTO.setStatus(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            responseDTO.setMessage(ERROR);
            responseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseDTO;
    }

    @PostMapping("/signingEmail")
    public ResponseDTO<?> signInWithEmail(@RequestBody SingInWithEmailRequestDTO signin) {
        ResponseDTO<SignInWithGoogleResponseDTO> responseDTO = new ResponseDTO<>();
        try {
            if (signin.getEmail() == null || signin.getPassword() == null || signin.getEmail().trim().isEmpty() || signin.getPassword().trim().isEmpty()) {
                responseDTO.setMessage(INVALID_EMAIL);
                responseDTO.setStatus(HttpStatus.BAD_REQUEST);
            }
            SignInWithGoogleResponseDTO response = singInWithGoogleService.authenticateWithEmail(signin.getEmail(), signin.getPassword());
            responseDTO.setData(response);
            responseDTO.setStatus(HttpStatus.OK);
            responseDTO.setMessage(SUCCESS);
        } catch (IllegalStateException e) {
            responseDTO.setMessage(NOT_FOUND);
            responseDTO.setStatus(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            responseDTO.setMessage(ERROR);
            responseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseDTO;
    }

    @PostMapping("/refresh-token")
    public ResponseDTO<?> refreshToken(@RequestBody String refreshToken) {
        ResponseDTO<TokenResponseDTO> responseDTO = new ResponseDTO<>();
        try {
            responseDTO = verificationService.rotationToken(refreshToken);
        } catch (Exception e) {
            responseDTO.setMessage(ERROR);
            responseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseDTO;
    }

    @PostMapping("/verify")
    public ResponseEntity<VerificationResponse> verifyToken(@RequestHeader(value = "Authorization", required = false) String token) {
        // Loại bỏ "Bearer " nếu token có định dạng "Bearer <token>"
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        VerificationResponse response = verificationService.verifyToken(token);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
