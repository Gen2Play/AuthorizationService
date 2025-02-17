package com.Gen2Play.AuthorizationService.service.authentication;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.Gen2Play.AuthorizationService.mapper.AccountMapper;
import com.Gen2Play.AuthorizationService.model.dto.response.SignInWithGoogleResponseDTO;
import com.Gen2Play.AuthorizationService.model.entity.Account;
import com.Gen2Play.AuthorizationService.model.entity.OAuthRefreshToken;
import com.Gen2Play.AuthorizationService.model.entity.Role;
import com.Gen2Play.AuthorizationService.model.entity.Role_Permission;
import com.Gen2Play.AuthorizationService.repository.user.IAccountRepository;
import com.Gen2Play.AuthorizationService.repository.user.IOAuthRefreshTokenRepository;
import com.Gen2Play.AuthorizationService.security.JwtTokenProvider;

@Service
public class SignInWithGoogleService implements ISingInWithGoogleService {

    private final IAccountRepository accountRepository;
    private final IOAuthRefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.access-token.expiration}")
    public long getJwtAccessExpiration;

    @Value("${jwt.refresh-token.expiration}")
    public long getJwtRefreshExpiration;

    public SignInWithGoogleService(IAccountRepository accountRepository,
            IOAuthRefreshTokenRepository refreshTokenRepository,
            JwtTokenProvider jwtTokenProvider) {
        this.accountRepository = accountRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public SignInWithGoogleResponseDTO authenticateWithGoogle(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Account not found for email: " + email));
        return authenticate(account);
    }

    private String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public SignInWithGoogleResponseDTO authenticateWithEmail(String email, String password) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Account not found for email: " + email));

        if (!account.getPassword().equals(password)) {
            throw new IllegalStateException("Invalid password for email: " + email);
        }

        return authenticate(account);
    }

    private SignInWithGoogleResponseDTO authenticate(Account account){
        SignInWithGoogleResponseDTO response = new SignInWithGoogleResponseDTO();
        try {
            response = AccountMapper.INSTANCE.accountToSignInWithGoogleResponseDTO(account);

            // Lấy Role duy nhất của Account
            Role role = account.getRole();
            if (role == null || !role.isStatus()) {
                throw new IllegalStateException("Account does not have a valid active role");
            }

            // Lấy tên Role và các quyền từ Role_Permissions
            String roleName = role.getRoleCode();
            Set<String> permissions = role.getRole_permissions().stream()
                    .filter(Role_Permission::isStatus)
                    .map(rolePermission -> rolePermission.getPermission().getAction())
                    .collect(Collectors.toSet());

            // Tạo JWT token với role và permissions
            String jwtToken = jwtTokenProvider.generateToken(
                    account.getEmail(),
                    roleName,
                    permissions
            );
            response.setJwtToken(jwtToken);

            Timestamp accessExpire = new Timestamp(Instant.now().plusMillis(getJwtAccessExpiration).toEpochMilli());
            Timestamp refreshExpire = new Timestamp(Instant.now().plusMillis(getJwtRefreshExpiration).toEpochMilli());

            // Tạo refresh token ngẫu nhiên
            String refreshToken = generateRefreshToken();
            // Lưu refresh token vào cơ sở dữ liệu
            OAuthRefreshToken oauthRefreshToken = new OAuthRefreshToken();
            oauthRefreshToken.setToken(refreshToken);
            oauthRefreshToken.setAccount(account);
            oauthRefreshToken.setExpiryDate(refreshExpire);

            refreshTokenRepository.save(oauthRefreshToken);
            response.setRefreshToken(refreshToken);
            response.setExp(accessExpire.getTime());
    } catch (Exception e) {
        System.out.println(e.getMessage());
    }
    return response;
    }
}
