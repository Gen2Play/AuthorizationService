package com.Gen2Play.AuthorizationService.repository.permission;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Gen2Play.AuthorizationService.model.entity.Permission_Category;

@Repository
public interface IPermissionCategoryRepository extends JpaRepository<Permission_Category, Long>{
    Optional<Permission_Category> findByPermissionCategoryName(String name);
}
