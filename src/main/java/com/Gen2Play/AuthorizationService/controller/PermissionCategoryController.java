package com.Gen2Play.AuthorizationService.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Gen2Play.AuthorizationService.model.dto.request.PermissionCategoryRequestDTO;
import com.Gen2Play.AuthorizationService.model.dto.response.OperationStatus;
import com.Gen2Play.AuthorizationService.model.dto.response.PermissionCategoryDTO;
import com.Gen2Play.AuthorizationService.service.permission.IPermisionCategoryService;

@RestController
@RequestMapping("/api/permission/category")
public class PermissionCategoryController {

    @Autowired
    private IPermisionCategoryService permisionCategoryService;

    @PreAuthorize("hasAnyAuthority('VIEW_PERMISSION_CATEGORY', 'ALL_ACCESS')")
    @GetMapping
    public ResponseEntity<List<PermissionCategoryDTO>> getPermissionCategories() {
        List<PermissionCategoryDTO> permissionCategories = permisionCategoryService.getAllPermissionCategory();
        if (permissionCategories.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(permissionCategories);
    }

    @PreAuthorize("hasAnyAuthority('VIEW_PERMISSION_CATEGORY', 'ALL_ACCESS')")
    @GetMapping("/{id}")
    public ResponseEntity<PermissionCategoryDTO> getPermissionCategoryById(@PathVariable Long id) {
        Optional<PermissionCategoryDTO> permissionCategory = permisionCategoryService.getPermissionCategoryById(id);
        if (permissionCategory.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(permissionCategory.get());
    }

    @PreAuthorize("hasAnyAuthority('VIEW_PERMISSION_CATEGORY', 'ALL_ACCESS')")
    @GetMapping("/getbyname/{name}")
    public ResponseEntity<PermissionCategoryDTO> getPermissionCategoryByName(@PathVariable String name) {
        Optional<PermissionCategoryDTO> permissionCategory = permisionCategoryService.getPermissionCategoryByName(name);
        if (permissionCategory.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(permissionCategory.get());
    }

    @PreAuthorize("hasAnyAuthority('CREATE_PERMISSION_CATEGORY', 'ALL_ACCESS')")
    @PostMapping("/create")
    public ResponseEntity<?> createPermissionCategory(@RequestBody PermissionCategoryRequestDTO permissionCategoryRequestDTO) {
        OperationStatus permissionCategory = permisionCategoryService.createPermissionCategory(permissionCategoryRequestDTO);
        
        return switch (permissionCategory) {
            case SUCCESS -> ResponseEntity.ok("Permission Category created successfully");
            case INVALID_INPUT -> ResponseEntity.badRequest().body("Invalid input data");
            case ALREADY_EXISTS -> ResponseEntity.status(409).body("Permission Category already exists");
            case FAILURE -> ResponseEntity.status(500).body("An error occurred while creating Permission Category");
            case NOT_FOUND -> ResponseEntity.status(404).body("Permission Category not found");
            default -> ResponseEntity.status(500).body("Unexpected error occurred");
        };
    }

    @PreAuthorize("hasAnyAuthority('UPDATE_PERMISSION_CATEGORY', 'ALL_ACCESS')")
    @PostMapping("/update")
    public ResponseEntity<?> updatePermissionCategory(@RequestBody PermissionCategoryRequestDTO permissionCategoryRequestDTO) {
        OperationStatus permissionCategory = permisionCategoryService.updatePermissionCategory(permissionCategoryRequestDTO);
        
        return switch (permissionCategory) {
            case SUCCESS -> ResponseEntity.ok("Permission Category updated successfully");
            case INVALID_INPUT -> ResponseEntity.badRequest().body("Invalid input data");
            case ALREADY_EXISTS -> ResponseEntity.status(409).body("Permission Category already exists");
            case FAILURE -> ResponseEntity.status(500).body("An error occurred while updating Permission Category");
            case NOT_FOUND -> ResponseEntity.status(404).body("Permission Category not found");
            default -> ResponseEntity.status(500).body("Unexpected error occurred");
        };
    }

    @PreAuthorize("hasAnyAuthority('DELETE_PERMISSION_CATEGORY', 'ALL_ACCESS')")
    @PostMapping("/delete/{categoryId}")
    public ResponseEntity<?> deletePermissionCategory(@PathVariable long categoryId) {
        OperationStatus permissionCategory = permisionCategoryService.deletePermissionCategory(categoryId);
        
        return switch (permissionCategory) {
            case SUCCESS -> ResponseEntity.ok("Permission Category deleted successfully");
            case FAILURE -> ResponseEntity.status(500).body("An error occurred while deleting Permission Category");
            case NOT_FOUND -> ResponseEntity.status(404).body("Permission Category not found");
            case CANNOT_DELETE -> ResponseEntity.status(400).body("Permission Category is in use");
            default -> ResponseEntity.status(500).body("Unexpected error occurred");
        };
    }
}
