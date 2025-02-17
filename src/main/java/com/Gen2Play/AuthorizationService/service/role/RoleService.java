package com.Gen2Play.AuthorizationService.service.role;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.Gen2Play.AuthorizationService.model.dto.request.RoleRequestDTO;
import com.Gen2Play.AuthorizationService.model.dto.response.OperationStatus;
import com.Gen2Play.AuthorizationService.model.dto.response.RoleResponseDTO;
import com.Gen2Play.AuthorizationService.model.entity.Account;
import com.Gen2Play.AuthorizationService.model.entity.Permission;
import com.Gen2Play.AuthorizationService.model.entity.Role;
import com.Gen2Play.AuthorizationService.model.entity.Role_Permission;
import com.Gen2Play.AuthorizationService.repository.permission.IPermissionRepository;
import com.Gen2Play.AuthorizationService.repository.role.IRolePermissionRepository;
import com.Gen2Play.AuthorizationService.repository.role.IRoleRepository;
import com.Gen2Play.AuthorizationService.repository.user.IAccountRepository;
import com.Gen2Play.AuthorizationService.utils.Util;
import com.Gen2Play.AuthorizationService.exception.Exception;
import com.Gen2Play.AuthorizationService.mapper.RoleMapper;

import jakarta.transaction.Transactional;

@Service
public class RoleService implements IRoleService {

    private final IRoleRepository roleRepository;
    private final IAccountRepository accountRepository;
    private final RolePermissionService rolePermissionService;
    private final IPermissionRepository permissionRepository;
    private final IRolePermissionRepository rolePermissionRepository;
    private final Util utils = new Util();

    public RoleService(IRoleRepository roleRepository,
            RolePermissionService rolePermissionService, IAccountRepository accountRepository,
            IPermissionRepository permissionRepository, IRolePermissionRepository rolePermissionRepository) {
        this.roleRepository = roleRepository;
        this.rolePermissionService = rolePermissionService;
        this.accountRepository = accountRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Override
    public List<RoleResponseDTO> getAllRoles() {
        try {
            List<RoleResponseDTO> roleResponseDTOs = new ArrayList<>();
            List<Role> roles = roleRepository.findAll();

            for (Role role : roles) {
                Optional<List<Account>> accounts = accountRepository.findAllByRoleRoleId(role.getRoleId());
                LocalDateTime lastUpdatedAt = role.getUpdatedAt() == null ? role.getCreatedAt() : role.getUpdatedAt();

                RoleResponseDTO roleResponse = new RoleResponseDTO();
                roleResponse.setRoleId(role.getRoleId());
                roleResponse.setRoleName(role.getRoleName());
                roleResponse.setRoleCode(role.getRoleCode());
                roleResponse.setStatus(role.isStatus());
                roleResponse.setDescription(role.getDescription());
                roleResponse.setLastUpdatedAt(lastUpdatedAt);
                roleResponse.setTotalUser(accounts.isPresent() ? accounts.get().size() : 0);
                roleResponseDTOs.add(roleResponse);
            }

            return roleResponseDTOs;
        } catch (Exception e) {
            throw new Exception("Error while getting all roles");
        }
    }

    @Override
    public List<RoleResponseDTO> getAllRolesByRole() throws Exception {
        try {
            Account account = utils.getAuthenticatedAccount();
            List<Role> roles = roleRepository.findAll();
            List<RoleResponseDTO> roleResponseDTOs = new ArrayList<>();

            String roleCode = account.getRole().getRoleCode();

            for (Role role : roles) {
                RoleResponseDTO roleResponse = new RoleResponseDTO();
                boolean status = false;

                if ("ADMIN".equals(roleCode)) {
                    status = true;
                } else if ("EXAMINER".equals(roleCode) && !"ADMIN".equals(role.getRoleCode())) {
                    status = true;
                } else if ("HEAD_OF_DEPARTMENT".equals(roleCode)
                        && (!"ADMIN".equals(role.getRoleCode()) || !"EXAMINER".equals(role.getRoleCode()))) {
                    status = true;
                }

                roleResponse.setStatus(status);
                roleResponse.setRoleId(role.getRoleId());
                roleResponse.setRoleName(role.getRoleName());
                roleResponseDTOs.add(roleResponse);
            }

            return roleResponseDTOs;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public RoleResponseDTO getRoleById(Long roleId) {
        try {
            Role role = roleRepository.findById(roleId).get();
            if (role == null) {
                return null;
            }

            return RoleMapper.INSTANCE.roleToRoleResponseDTO(role);
        } catch (Exception e) {
            throw new Exception("Error while getting role with id: " + roleId);
        }
    }

    @Override
    @Transactional
    public OperationStatus createRole(RoleRequestDTO roleRequestDTO) {
        try {
            String roleName = roleRequestDTO.getRoleName().trim();
            String roleCode = roleRequestDTO.getRoleCode().toUpperCase().trim().replace(" ", "_");
            if (roleCode == null || roleCode.isEmpty()) {
                return OperationStatus.INVALID_INPUT;
            }

            Optional<Role> role = roleRepository.findByRoleName(roleName);
            if (role.isPresent() && role.get().isStatus()) {
                return OperationStatus.ALREADY_EXISTS;
            }
            Optional<Role> roleByCode = roleRepository.findByRoleCode(roleCode);
            if (roleByCode.isPresent() && roleByCode.get().isStatus()) {
                return OperationStatus.ALREADY_EXISTS;
            }

            Role roleEntity = new Role();
            roleEntity.setRoleCode(roleCode);
            roleEntity.setDescription(roleRequestDTO.getDescription());
            roleEntity.setRoleName(roleName);
            roleEntity.setStatus(true);
            roleEntity.setCreatedAt(Util.getCurrentDateTime());
            roleEntity.setCreatedBy(Util.getAuthenticatedAccountId());
            Role savedRole = roleRepository.save(roleEntity);

            List<Permission> permissions = permissionRepository.findAll();
            for (Permission permission : permissions) {
                rolePermissionService.createRolePermission(permission, savedRole);
            }

            if (savedRole == null || savedRole.getRoleId() == null) {
                return OperationStatus.FAILURE;
            }

            return OperationStatus.SUCCESS;
        } catch (Exception e) {
            return OperationStatus.ERROR;
        }
    }

    @Override
    public OperationStatus updateRole(RoleRequestDTO roleRequestDTO) {
        try {
            Long roleId = roleRequestDTO.getRoleId();
            String roleName = roleRequestDTO.getRoleName().trim();
            String roleCode = roleRequestDTO.getRoleCode().toUpperCase().trim().replace(" ", "_");
            if (roleId == null || roleId <= 0 || roleName == null || roleName.isEmpty() || roleCode == null || roleCode.isEmpty()) {
                return OperationStatus.INVALID_INPUT;
            }

            Optional<Role> role = roleRepository.findById(roleId);
            if (role.isEmpty()) {
                return OperationStatus.NOT_FOUND;
            }

            List<Role> roles = roleRepository.findAll();
            for (Role r : roles) {
                if (!Objects.equals(r.getRoleId(), roleId) && r.getRoleName().equals(roleName)) {
                    return OperationStatus.ALREADY_EXISTS;
                }

                if (!Objects.equals(r.getRoleId(), roleId) && r.getRoleCode().equals(roleCode)) {
                    return OperationStatus.ALREADY_EXISTS;
                }
            }

            role.get().setRoleName(roleName);
            role.get().setDescription(roleRequestDTO.getDescription());
            role.get().setRoleCode(roleCode);
            role.get().setUpdatedAt(Util.getCurrentDateTime());
            role.get().setUpdatedBy(Util.getAuthenticatedAccountId());
            Role savedRole = roleRepository.save(role.get());
            if (savedRole == null || savedRole.getRoleId() == null) {
                return OperationStatus.FAILURE;
            }

            return OperationStatus.SUCCESS;
        } catch (Exception e) {
            return OperationStatus.ERROR;
        }
    }

    @Override
    @Transactional
    public OperationStatus deleteRole(Long roleId) {
        try {
            Optional<Role> role = roleRepository.findById(roleId);
            if (role.isEmpty()) {
                return OperationStatus.NOT_FOUND;
            }

            Optional<List<Account>> accounts = accountRepository.findAllByRoleRoleId(roleId);
            if (accounts.isPresent() && !accounts.get().isEmpty()) {
                return OperationStatus.CANNOT_DELETE;
            }

            List<Role_Permission> rolePermissions = rolePermissionRepository.findAllByRole_RoleId(roleId);
            for (Role_Permission rolePermission : rolePermissions) {
                rolePermissionRepository.delete(rolePermission);
            }

            roleRepository.deleteById(roleId);
            return OperationStatus.SUCCESS;
        } catch (Exception e) {
            return OperationStatus.ERROR;
        }
    }

    @Override
    public RoleResponseDTO getRoleByName(String roleName) {
        try {
            Optional<Role> role = roleRepository.findByRoleName(roleName);
            if (role == null) {
                throw new Exception("Role not found with name: " + roleName);
            }

            return RoleMapper.INSTANCE.roleToRoleResponseDTO(role.get());
        } catch (Exception e) {
            throw new Exception("Error while getting role with name: " + roleName);
        }
    }

}
