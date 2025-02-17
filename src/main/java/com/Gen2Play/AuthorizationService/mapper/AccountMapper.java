package com.Gen2Play.AuthorizationService.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.Gen2Play.AuthorizationService.model.dto.response.SignInWithGoogleResponseDTO;
import com.Gen2Play.AuthorizationService.model.entity.Account;

@Mapper
public interface AccountMapper {
    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    @Mapping(source = "accountId", target = "id")
    @Mapping(expression = "java(getRoleName(account))", target = "role")
    @Mapping(expression = "java(getPermissions(account))", target = "permissions")
    SignInWithGoogleResponseDTO accountToSignInWithGoogleResponseDTO(Account account);

    default String getRoleName(Account account) {
        return account.getRole() != null ? account.getRole().getRoleCode() : "Unknown";
    }

    default Set<String> getPermissions(Account account) {
        return account.getRole() != null ? account.getRole().getRole_permissions().stream()
                .filter(rolePermission -> rolePermission.isStatus() && rolePermission.getPermission() != null)
                .map(rolePermission -> rolePermission.getPermission().getAction())
                .collect(Collectors.toSet()) : Set.of();
    }
}
