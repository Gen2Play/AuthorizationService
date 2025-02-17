package com.Gen2Play.AuthorizationService.service.account;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.Gen2Play.AuthorizationService.model.dto.request.AccountRequestDTO;
import com.Gen2Play.AuthorizationService.model.dto.response.AccountResponseDTO;
import com.Gen2Play.AuthorizationService.model.dto.response.OperationStatus;
import com.Gen2Play.AuthorizationService.model.entity.Account;
import com.Gen2Play.AuthorizationService.model.entity.Role;
import com.Gen2Play.AuthorizationService.repository.role.IRoleRepository;
import com.Gen2Play.AuthorizationService.repository.user.IAccountRepository;
import com.Gen2Play.AuthorizationService.utils.Util;

@Service
public class AccountService implements IAccountService {

    private final IAccountRepository accountRepository;
    private final IRoleRepository roleRepository;

    public AccountService(IAccountRepository accountRepository, IRoleRepository roleRepository) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public List<AccountResponseDTO> getAllAccount() {
        try {
            Account acc = accountRepository.findById(Util.getAuthenticatedAccountId()).get();

            List<Account> accountByRole = new ArrayList<>();
            if (null == acc.getRole().getRoleCode()) {
                List<Account> accounts = accountRepository.findAll();
                for (Account account : accounts) {
                    if (!account.getRole().getRoleCode().equals("ADMIN") && !account.getRole().getRoleCode().equals("EXAMINER")
                            && !account.getRole().getRoleCode().equals("HEAD_OF_DEPARTMENT")) {
                        accountByRole.add(account);
                    }
                }
            } else {
                switch (acc.getRole().getRoleCode()) {
                    case "ADMIN" ->
                        accountByRole = accountRepository.findAll();
                    case "EXAMINER" -> {
                        List<Account> accounts = accountRepository.findAll();
                        for (Account account : accounts) {
                            if (!account.getRole().getRoleCode().equals("ADMIN")) {
                                accountByRole.add(account);
                            }
                        }
                    }
                    case "HEAD_OF_DEPARTMENT" -> {
                        List<Account> accounts = accountRepository.findAll();
                        for (Account account : accounts) {
                            if (!account.getRole().getRoleCode().equals("ADMIN") && !account.getRole().getRoleCode().equals("EXAMINER")) {
                                accountByRole.add(account);
                            }
                        }
                    }
                    default -> {
                        List<Account> accounts = accountRepository.findAll();
                        for (Account account : accounts) {
                            if (!account.getRole().getRoleCode().equals("ADMIN") && !account.getRole().getRoleCode().equals("EXAMINER")
                                    && !account.getRole().getRoleCode().equals("HEAD_OF_DEPARTMENT")) {
                                accountByRole.add(account);
                            }
                        }
                    }
                }
            }

            List<AccountResponseDTO> accountResponseDTOs = new ArrayList<>();
            for (Account account : accountByRole) {
                AccountResponseDTO accountResponseDTO = new AccountResponseDTO();
                accountResponseDTO.setAccountId(account.getAccountId());
                accountResponseDTO.setEmail(account.getEmail());
                accountResponseDTO.setRole(account.getRole().getRoleName());
                accountResponseDTO.setStatus(account.isStatus() ? "Active" : "Inactive");accountResponseDTO.setCreatedAt(account.getCreatedAt());
                accountResponseDTO.setUpdatedAt(account.getUpdatedAt());
                accountResponseDTO.setDeletedAt(account.getDeletedAt());
                accountResponseDTOs.add(accountResponseDTO);
            }

            return accountResponseDTOs.stream().sorted(Comparator.comparing(AccountResponseDTO::getCreatedAt).reversed()).collect(Collectors.toList());

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public AccountResponseDTO getAccountById(UUID accountId) {
        try {
            Account account = accountRepository.findById(accountId).get();
            AccountResponseDTO accountResponseDTO = new AccountResponseDTO();
            accountResponseDTO.setAccountId(account.getAccountId());
            accountResponseDTO.setEmail(account.getEmail());
            accountResponseDTO.setRole(account.getRole().getRoleName());
            accountResponseDTO.setRoleId(account.getRole().getRoleId());
            return accountResponseDTO;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public OperationStatus createAccount(AccountRequestDTO accountRequestDTO) {
        try {
            if (validateInput(accountRequestDTO, "create")) {
                List<AccountResponseDTO> accounts = getAllAccount();
                for (AccountResponseDTO account : accounts) {
                    if (accountRequestDTO.getEmail().equals(account.getEmail())) {
                        return OperationStatus.ALREADY_EXISTS;
                    }
                }
                Account account = new Account();
                Role role = roleRepository.findById(accountRequestDTO.getRoleId()).get();
                account.setEmail(accountRequestDTO.getEmail());
                account.setRole(role);
                account.setCreatedAt(Util.getCurrentDateTime());
                account.setCreatedBy(Util.getAuthenticatedAccountId());
                account.setPassword(generatePassword(12));
                account.setStatus(true);
                accountRepository.save(account);
                return OperationStatus.SUCCESS;
            }
        } catch (Exception e) {
            return OperationStatus.ERROR;
        }
        return OperationStatus.FAILURE;
    }

    @Override
    public OperationStatus updateAccount(AccountRequestDTO accountRequestDTO) {
        try {
            if (validateInput(accountRequestDTO, "update")) {
                List<AccountResponseDTO> accounts = getAllAccount();
                for (AccountResponseDTO account : accounts) {
                    if (!accountRequestDTO.getEmail().equals(account.getEmail())
                            || Objects.equals(accountRequestDTO.getAccountId(), account.getAccountId())) {
                    } else {
                        return OperationStatus.ALREADY_EXISTS;
                    }
                }
                Account account = accountRepository.findById(accountRequestDTO.getAccountId()).get();
                account.setEmail(accountRequestDTO.getEmail());
                account.setRole(roleRepository.findById(accountRequestDTO.getRoleId()).get());
                account.setUpdatedBy(Util.getAuthenticatedAccountId());
                accountRepository.save(account);

                return OperationStatus.SUCCESS;
            }
        } catch (Exception e) {
            return OperationStatus.ERROR;
        }
        return OperationStatus.FAILURE;
    }

    @Override
    public OperationStatus deleteAccount(UUID accountId) {
        try {
            Account account = accountRepository.findById(accountId).get();
            account.setDeletedBy(Util.getAuthenticatedAccountId());
            account.setDeletedAt(Util.getCurrentDateTime());
            account.setStatus(!account.isStatus());
            accountRepository.save(account);

            return OperationStatus.SUCCESS;
        } catch (Exception e) {
            return OperationStatus.ERROR;
        }
    }

    @Override
    public OperationStatus updateProfile(AccountRequestDTO accountRequestDTO) {
        try {
            if (validateInput(accountRequestDTO, "updateProfile")) {
                return OperationStatus.FAILURE;
            }
            List<AccountResponseDTO> accounts = getAllAccount();
            UUID accountId = Util.getAuthenticatedAccountId();
            for (AccountResponseDTO account : accounts) {
                if (accountRequestDTO.getEmail().equals(account.getEmail())
                        && !Objects.equals(accountId, account.getAccountId())) {
                    return OperationStatus.ALREADY_EXISTS;
                }
            }
            Account account = accountRepository.findById(accountId).orElseThrow();
            account.setUpdatedBy(accountId);
            if (accountRequestDTO.getOldPassword() != null) {
                if (accountRequestDTO.getNewPassword().equals(accountRequestDTO.getConfirmPassword())) {
                    account.setPassword(accountRequestDTO.getNewPassword());
                }
            }
            accountRepository.save(account);

            return OperationStatus.SUCCESS;
        } catch (Exception e) {
            return OperationStatus.ERROR;
        }
    }

    private boolean validateInput(AccountRequestDTO accountRequestDTO, String type) {
        if (accountRequestDTO == null || type == null) {
            return false;
        }

        boolean hasRequiredFields
                = accountRequestDTO.getName() != null && !accountRequestDTO.getName().trim().isEmpty()
                && accountRequestDTO.getEmail() != null && !accountRequestDTO.getEmail().trim().isEmpty()
                && accountRequestDTO.getRoleId() != null;
        boolean hasRequiredFieldsForUpdate
                = accountRequestDTO.getName() != null && !accountRequestDTO.getName().trim().isEmpty()
                && accountRequestDTO.getEmail() != null && !accountRequestDTO.getEmail().trim().isEmpty();
        switch (type) {
            case "create" -> {
                return hasRequiredFields;
            }
            case "update" -> {
                return hasRequiredFields && accountRequestDTO.getAccountId() != null;
            }
            case "updateProfile" -> {
                return hasRequiredFieldsForUpdate && accountRequestDTO.getAccountId() != null;
            }
            default -> {
            }
        }
        return false;
    }

    private String generatePassword(int length) {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{}|;:,.<>?";
        SecureRandom random = new SecureRandom();

        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }
        return password.toString();
    }
}
