package com.Gen2Play.AuthorizationService.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.Gen2Play.AuthorizationService.model.dto.response.RoleResponseDTO;
import com.Gen2Play.AuthorizationService.model.entity.Role;

@Mapper
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class); 
    RoleResponseDTO roleToRoleResponseDTO(Role role);
}
