package com.Gen2Play.AuthorizationService.service.role;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Gen2Play.AuthorizationService.model.dto.request.RolePermissionRequestDTO;
import com.Gen2Play.AuthorizationService.model.dto.response.OperationStatus;
import com.Gen2Play.AuthorizationService.model.dto.response.PermissionCategoryResponseDTO;
import com.Gen2Play.AuthorizationService.model.dto.response.PermissionListResponseDTO;
import com.Gen2Play.AuthorizationService.model.dto.response.RolePermissionResponseDTO;
import com.Gen2Play.AuthorizationService.model.entity.Permission;
import com.Gen2Play.AuthorizationService.model.entity.Role;
import com.Gen2Play.AuthorizationService.model.entity.Role_Permission;
import com.Gen2Play.AuthorizationService.repository.permission.IPermissionRepository;
import com.Gen2Play.AuthorizationService.repository.role.IRolePermissionRepository;
import com.Gen2Play.AuthorizationService.repository.role.IRoleRepository;
import com.Gen2Play.AuthorizationService.repository.user.IAccountRepository;
import com.Gen2Play.AuthorizationService.service.permission.PermissionService;
import com.Gen2Play.AuthorizationService.utils.Util;
import com.Gen2Play.AuthorizationService.exception.Exception;

@Service
public class RolePermissionService implements IRolePermissionService {

    private final IRolePermissionRepository rolePermissionRepository;
    private final IRoleRepository roleRepository;
    private final IPermissionRepository permissionRepository;
    private final PermissionService permissionService;

    public RolePermissionService(IRolePermissionRepository rolePermissionRepository, IRoleRepository roleRepository,
            IPermissionRepository permissionRepository, IAccountRepository accountRepository,
            @Lazy PermissionService permissionService) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.permissionService = permissionService;
    }

    @Override
    public RolePermissionResponseDTO getRolePermissionById(Long id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("Id cannot be null");
            }
            Role role = roleRepository.findById(id).get();
            List<Role_Permission> rolePermissions = rolePermissionRepository.findAllByRole_RoleId(id);
            if (rolePermissions == null || rolePermissions.isEmpty()) {
                return null;
            }

            List<PermissionCategoryResponseDTO> permissionCategories = permissionService.getAllPermissionByPermissionCategory();
            if (permissionCategories == null || permissionCategories.isEmpty()) {
                return null;
            }

            for (Role_Permission rolePermission : rolePermissions) {
                for (PermissionCategoryResponseDTO permissionCategory : permissionCategories) {
                    for (PermissionListResponseDTO permission : permissionCategory.getPermissions()) {
                        if (rolePermission.getPermission().getPermissionId().equals(permission.getPermissionId())) {
                            permission.setStatus(rolePermission.isStatus());
                        }
                    }
                }
            }

            RolePermissionResponseDTO rolePermissionResponseDTO = new RolePermissionResponseDTO();
            rolePermissionResponseDTO.setRoleId(role.getRoleId());
            rolePermissionResponseDTO.setRoleName(role.getRoleName());
            rolePermissionResponseDTO.setRoleCode(role.getRoleCode());
            rolePermissionResponseDTO.setDescription(role.getDescription());
            rolePermissionResponseDTO.setStatus(role.isStatus());
            rolePermissionResponseDTO.setPermissionsCategory(permissionCategories);
            return rolePermissionResponseDTO;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public OperationStatus createRolePermission(Permission permission, Role role) {
        try {
            Role_Permission rolePermission = new Role_Permission();

            rolePermission.setRole(role);
            rolePermission.setPermission(permission);
            if (role.getRoleCode().equals("ADMIN")) {
                rolePermission.setStatus(true);
            } else {
                rolePermission.setStatus(false);
            }
            rolePermission.setCreatedAt(LocalDateTime.now());
            rolePermission.setCreatedBy(Util.getAuthenticatedAccountId());

            Role_Permission savedRolePermission = rolePermissionRepository.save(rolePermission);
            if (savedRolePermission == null || savedRolePermission.getRolePermissionId() == null) {
                return OperationStatus.FAILURE;
            }

            return OperationStatus.SUCCESS;
        } catch (Exception e) {
            return OperationStatus.ERROR;
        }
    }

    @Override
    @Transactional
    public OperationStatus updateRolePermission(RolePermissionRequestDTO rolePermissionRequestDTO) {
        try {
            Optional<Role> role = roleRepository.findById(rolePermissionRequestDTO.getRoleId());
            if (role.isEmpty()) {
                return OperationStatus.NOT_FOUND;
            }
            Optional<Permission> permission = permissionRepository.findById(rolePermissionRequestDTO.getPermissionId());
            if (permission.isEmpty()) {
                return OperationStatus.NOT_FOUND;
            }
            Optional<Role_Permission> rolePermission
                    = rolePermissionRepository.findByRole_RoleIdAndPermission_PermissionId(
                            rolePermissionRequestDTO.getRoleId(), rolePermissionRequestDTO.getPermissionId());
            if (rolePermission.isEmpty()) {
                return OperationStatus.NOT_FOUND;
            }
            rolePermission.get().setStatus(rolePermissionRequestDTO.isStatus());
            rolePermissionRepository.save(rolePermission.get());
            return OperationStatus.SUCCESS;
        } catch (Exception e) {
            return OperationStatus.ERROR;
        }
    }
}
