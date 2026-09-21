package com.gms.gmsmvp.repository;

import com.gms.gmsmvp.entity.RolePermissionMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RolePermissionMappingRepository extends JpaRepository<RolePermissionMapping, Long> {

    @Query("""
            select rpm.permission.permissionCode
            from RolePermissionMapping rpm
            where rpm.role.id = :roleId
            """)
    List<String> findPermissionCodesByRoleId(
            @Param("roleId") Long roleId
    );
}