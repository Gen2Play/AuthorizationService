package com.Gen2Play.AuthorizationService.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.Gen2Play.AuthorizationService.model.dto.response.PermissionResponseDTO;
import com.Gen2Play.AuthorizationService.model.entity.Permission;

@Mapper
public interface PermissionMapper {
    PermissionMapper INSTANCE = Mappers.getMapper(PermissionMapper.class);

    @Mapping(source = "permissionId", target = "permissionId")
    @Mapping(source = "permissionName", target = "permissionName")
    @Mapping(source = "action", target = "action")
    @Mapping(source = "permissionCategory", target = "permissionCategory")
    PermissionResponseDTO permissionToPermissionResponseDTO(Permission permission);
}
