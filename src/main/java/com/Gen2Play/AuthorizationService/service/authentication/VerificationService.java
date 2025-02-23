package com.Gen2Play.AuthorizationService.service.authentication;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.Gen2Play.AuthorizationService.model.dto.response.ResponseDTO;
import com.Gen2Play.AuthorizationService.model.dto.response.TokenResponseDTO;
import com.Gen2Play.AuthorizationService.model.entity.Account;
import com.Gen2Play.AuthorizationService.model.entity.OAuthRefreshToken;
import com.Gen2Play.AuthorizationService.model.entity.Role;
import com.Gen2Play.AuthorizationService.model.entity.Role_Permission;
import com.Gen2Play.AuthorizationService.repository.user.IOAuthRefreshTokenRepository;
import com.Gen2Play.AuthorizationService.security.JwtTokenProvider;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

@Service
public class VerificationService {

    private final JwtTokenProvider jwtTokenProvider;
    private final IOAuthRefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.access-token.expiration}")
    public long getJwtAccessExpiration;

    @Value("${jwt.refresh-token.expiration}")
    public long getJwtRefreshExpiration;

    public VerificationService(JwtTokenProvider jwtTokenProvider, IOAuthRefreshTokenRepository refreshTokenRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public VerificationResponse verifyToken(String token) {
        try {
            if (jwtTokenProvider.validateToken(token)) {
                return new VerificationResponse(HttpStatus.OK, "Token is valid.");
            } else {
                return new VerificationResponse(HttpStatus.FORBIDDEN, "Token has been revoked or invalid.");
            }
        } catch (ExpiredJwtException e) {
            return new VerificationResponse(HttpStatus.UNAUTHORIZED, "Token is expired. Details: " + e.getClaims());
        } catch (MalformedJwtException e) {
            return new VerificationResponse(HttpStatus.BAD_REQUEST, "Malformed token: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            return new VerificationResponse(HttpStatus.BAD_REQUEST, "Unsupported token format.");
        } catch (IllegalArgumentException e) {
            return new VerificationResponse(HttpStatus.BAD_REQUEST, "Token claims string is empty or null.");
        } catch (JwtException e) {
            return new VerificationResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error during token validation: " + e.getMessage());
        }
    }

    public ResponseDTO<TokenResponseDTO> rotationToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                .filter(oauthRefreshToken -> oauthRefreshToken.getExpiryDate().after(new Timestamp(System.currentTimeMillis())))
                .map(oauthRefreshToken -> {
                    Account account = oauthRefreshToken.getAccount();
                    if (account == null || account.getAccountId() == null) {
                        throw new IllegalStateException("Account or account_id cannot be null");
                    }

                    // Thu hồi refresh token cũ
                    refreshTokenRepository.delete(oauthRefreshToken);

                    Role role = account.getRole();
                    if (role == null || role.getRoleCode() == null) {
                        throw new IllegalStateException("Account does not have a valid active role");
                    }

                    // Tạo danh sách quyền
                    Set<String> permissions = role.getRole_permissions().stream()
                            .filter(Role_Permission::isStatus)
                            .map(rolePermission -> rolePermission.getPermission().getAction())
                            .collect(Collectors.toSet());

                    // Tạo access token mới
                    String newAccessToken = jwtTokenProvider.generateAccessToken(
                            account.getAccountId(),
                            role.getRoleCode(),
                            permissions
                    );
                    
                    Timestamp accessExpire = new Timestamp(Instant.now().plusMillis(getJwtAccessExpiration).toEpochMilli());
                    Timestamp refreshExpire = new Timestamp(Instant.now().plusMillis(getJwtRefreshExpiration).toEpochMilli());

                    // Tạo refresh token mới
                    OAuthRefreshToken newRefreshToken = new OAuthRefreshToken();
                    newRefreshToken.setToken(jwtTokenProvider.generateRefreshToken(account.getAccountId()));
                    newRefreshToken.setAccount(account);
                    newRefreshToken.setExpiryDate(refreshExpire);
                    refreshTokenRepository.save(newRefreshToken);

                    // Trả về access token và refresh token mới
                    ResponseDTO<TokenResponseDTO> response = new ResponseDTO<>();
                    TokenResponseDTO tokenResponseDTO = new TokenResponseDTO(newAccessToken,
                            newRefreshToken.getToken(),
                            accessExpire.getTime());
                            response.setData(tokenResponseDTO);
                            response.setStatus(200);
                            response.setMessage("Get new token successfully.");
                    return response;
                })
                .orElseGet(() -> {
                    ResponseDTO<TokenResponseDTO> response = new ResponseDTO<>();
                    response.setStatus(403);
                    response.setMessage("Refresh token is invalid.");
                    return response;
                });
    }
}
