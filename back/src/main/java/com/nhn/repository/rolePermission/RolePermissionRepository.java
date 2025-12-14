package com.nhn.repository.rolePermission;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nhn.model.entity.rolePermission.RolePermission;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermission.PrimaryKeys> {

    //    boolean existsByRoleIdAndPermissionId(UUID roleId, UUID permissionId);
    //
    //    List<RolePermission> findByRoleId(UUID roleId);

    @Modifying
    @Query("DELETE FROM RolePermission rp WHERE rp.roleId = :roleId AND rp.permissionId IN (:permissionIds)")
    void deleteByRoleIdAndPermissionIds(UUID roleId, List<UUID> permissionIds);
}
