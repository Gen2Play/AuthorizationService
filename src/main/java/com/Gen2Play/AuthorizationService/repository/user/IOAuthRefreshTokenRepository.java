package com.Gen2Play.AuthorizationService.repository.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Gen2Play.AuthorizationService.model.entity.Account;
import com.Gen2Play.AuthorizationService.model.entity.OAuthRefreshToken;

public interface IOAuthRefreshTokenRepository extends JpaRepository<OAuthRefreshToken, Long>{
    // Tìm refresh token bằng giá trị token
    Optional<OAuthRefreshToken> findByToken(String token);

    // Xóa tất cả các refresh token cho một tài khoản
    void deleteByAccount(Account account);
}
