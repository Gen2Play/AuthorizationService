package com.Gen2Play.AuthorizationService.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Gen2Play.AuthorizationService.model.dto.request.AccountRequestDTO;
import com.Gen2Play.AuthorizationService.model.dto.response.AccountResponseDTO;
import com.Gen2Play.AuthorizationService.model.dto.response.OperationStatus;
import com.Gen2Play.AuthorizationService.model.dto.response.ResponseDTO;
import com.Gen2Play.AuthorizationService.service.account.IAccountService;

@RestController
@RequestMapping("api/account/")
public class AccountController {

    private final IAccountService accountService;
    private final String ERROR = "An error occurred";

    public AccountController(IAccountService accountService) {
        this.accountService = accountService;
    }

    @PreAuthorize("hasAnyAuthority('VIEW_ACCOUNT', 'ALL_ACCESS')")
    @GetMapping
    public ResponseDTO<?> getAllAccounts() {
        ResponseDTO<List<AccountResponseDTO>> response = new ResponseDTO<>();
        try {
            List<AccountResponseDTO> accounts = accountService.getAllAccount();
            if (accounts.isEmpty()) {
                response.setMessage("Fail to get all account");
                response.setStatus(404);
            }
            response.setData(accounts);
            response.setMessage("Get all account successfully!");
            response.setStatus(200);
        } catch (Exception e) {
            response.setMessage(ERROR);
            response.setStatus(500);
        }
        return response;
    }

    @PreAuthorize("hasAnyAuthority('VIEW_PROFILE', 'ALL_ACCESS')")
    @GetMapping("profile")
    public ResponseDTO<?> getProfileById(@RequestParam UUID id) {
        ResponseDTO<AccountResponseDTO> response = new ResponseDTO<>();
        try {
            AccountResponseDTO account = accountService.getAccountById(id);
            if (account == null) {
                response.setMessage("Fail to get profile!");
                response.setStatus(404);
            }
            response.setData(account);
            response.setMessage("Get profile successfully!");
            response.setStatus(200);
        } catch (Exception e) {
            response.setMessage(ERROR);
            response.setStatus(500);
        }
        return response;
    }

    @PreAuthorize("hasAnyAuthority('UPDATE_PROFILE', 'ALL_ACCESS')")
    @PostMapping("profile/update")
    public ResponseDTO<?> updateProfile(@ModelAttribute AccountRequestDTO account) {
        ResponseDTO<OperationStatus> response = new ResponseDTO<>();
        try {
            OperationStatus operationStatus = accountService.updateProfile(account);
            switch (operationStatus) {
                case SUCCESS -> {
                    response.setMessage("Update profile successfully!");
                    response.setStatus(200);
                }
                case ALREADY_EXISTS ->
                {
                    response.setMessage("Account already exists!");
                    response.setStatus(409);
                }
                case FAILURE ->
                {
                    response.setMessage("Fail to update profile!");
                    response.setStatus(400);
                }
                default ->
                {
                    response.setMessage(ERROR);
                    response.setStatus(500);
                }
            }
        } catch (Exception e) {
            response.setMessage(ERROR);
            response.setStatus(500);
        }
        return response;
    }


    @PreAuthorize("hasAnyAuthority('CREATE_ACCOUNT', 'ALL_ACCESS')")
    @PostMapping("create")
    public ResponseDTO<?> createAccount(@RequestBody AccountRequestDTO account) {
        ResponseDTO<OperationStatus> response = new ResponseDTO<>();
        try {
            OperationStatus operationStatus = accountService.createAccount(account);
            switch (operationStatus) {
                case SUCCESS ->
                    {
                        response.setMessage("Create account successfully!");
                        response.setStatus(200);
                    }
                case ALREADY_EXISTS ->
                    {
                        response.setMessage("Account already exists!");
                        response.setStatus(409);
                    }
                case FAILURE ->
                    {
                        response.setMessage("Fail to create account!");
                        response.setStatus(400);
                    }
                default ->
                    {
                        response.setMessage(ERROR);
                        response.setStatus(500);
                    }
            }
        } catch (Exception e) {
            response.setMessage(ERROR);
            response.setStatus(500);
        }
        return response;
    }

    @PreAuthorize("hasAnyAuthority('UPDATE_ACCOUNT', 'ALL_ACCESS')")
    @PostMapping("update")
    public ResponseDTO<?> updateAccount(@RequestBody AccountRequestDTO account) {
        ResponseDTO<OperationStatus> response = new ResponseDTO<>();
        try {
            OperationStatus operationStatus = accountService.updateAccount(account);
            switch (operationStatus) {
                case SUCCESS ->
                    {
                        response.setMessage("Update account successfully!");
                        response.setStatus(200);
                    }
                case ALREADY_EXISTS ->
                    {
                        response.setMessage("Account already exists!");
                        response.setStatus(409);
                    }
                case FAILURE ->
                    {
                        response.setMessage("Fail to update account!");
                        response.setStatus(400);
                    }
                default ->
                    {
                        response.setMessage(ERROR);
                        response.setStatus(500);
                    }
            }
        } catch (Exception e) {
            response.setMessage(ERROR);
            response.setStatus(500);
        }
        return response;
    }

    @PreAuthorize("hasAnyAuthority('DELETE_ACCOUNT', 'ALL_ACCESS')")
    @PostMapping("delete/{id}")
    public ResponseDTO<?> deleteAccount(@PathVariable UUID id) {
        ResponseDTO<OperationStatus> response = new ResponseDTO<>();
        try {
            OperationStatus operationStatus = accountService.deleteAccount(id);
            switch (operationStatus) {
                case SUCCESS ->
                    {
                        response.setMessage("Delete account successfully!");
                        response.setStatus(200);
                    }
                default ->
                    {
                        response.setMessage(ERROR);
                        response.setStatus(500);
                    }
            }
        } catch (Exception e) {
            response.setMessage(ERROR);
            response.setStatus(500);
        }
        return response;
    }
}

