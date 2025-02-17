package com.Gen2Play.AuthorizationService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Gen2Play.AuthorizationService.model.dto.request.RolePermissionRequestDTO;
import com.Gen2Play.AuthorizationService.model.dto.response.OperationStatus;
import com.Gen2Play.AuthorizationService.model.dto.response.RolePermissionResponseDTO;
import com.Gen2Play.AuthorizationService.service.role.IRolePermissionService;

@RestController
@RequestMapping("/api/role-permission")
public class RolePermisisionController {
    private final IRolePermissionService rolePermissionService;

    public RolePermisisionController(IRolePermissionService rolePermissionService) {
        this.rolePermissionService = rolePermissionService;
    }

    @PreAuthorize("hasAnyAuthority('VIEW_ROLE', 'ALL_ACCESS')")
    @GetMapping("/{id}")
    public ResponseEntity<RolePermissionResponseDTO> getRolePermissionById(@PathVariable Long id) {
        RolePermissionResponseDTO rolePermissionResponseDTO = rolePermissionService.getRolePermissionById(id);
        if (rolePermissionResponseDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rolePermissionResponseDTO);
    }

    @PreAuthorize("hasAnyAuthority('UPDATE_ROLE', 'ALL_ACCESS')")
    @PostMapping("/update")
    public ResponseEntity<?> updateRolePermission(@RequestBody RolePermissionRequestDTO rolePermissionRequestDTO) {
        OperationStatus operationStatus = rolePermissionService.updateRolePermission(rolePermissionRequestDTO);
        return switch (operationStatus) {
            case SUCCESS -> ResponseEntity.ok("Role Permission updated successfully");
            case INVALID_INPUT -> ResponseEntity.badRequest().body("Invalid input data");
            case ALREADY_EXISTS -> ResponseEntity.status(409).body("Role Permission already exists");
            case FAILURE -> ResponseEntity.status(500).body("Can't update Role Permission");
            case NOT_FOUND -> ResponseEntity.status(404).body("Role Permission not found");
            case ERROR -> ResponseEntity.status(500).body("An error occurred while updating Role Permission");
            default -> ResponseEntity.status(500).body("Unexpected error occurred");
        };
    }
}
