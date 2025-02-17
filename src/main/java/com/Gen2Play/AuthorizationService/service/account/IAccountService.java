package com.Gen2Play.AuthorizationService.service.account;

import java.util.List;
import java.util.UUID;

import com.Gen2Play.AuthorizationService.model.dto.request.AccountRequestDTO;
import com.Gen2Play.AuthorizationService.model.dto.response.AccountResponseDTO;
import com.Gen2Play.AuthorizationService.model.dto.response.OperationStatus;


public interface IAccountService {
    List<AccountResponseDTO> getAllAccount();
    AccountResponseDTO getAccountById(UUID accountId);
    OperationStatus createAccount(AccountRequestDTO accountRequestDTO);
    OperationStatus updateAccount(AccountRequestDTO accountRequestDTO);
    OperationStatus deleteAccount(UUID accountId);
    OperationStatus updateProfile(AccountRequestDTO accountRequestDTO);
}
