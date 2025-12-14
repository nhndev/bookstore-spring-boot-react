package com.nhn.repository.rolePermission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nhn.model.dto.response.permission.PermissionInfo;
import com.nhn.model.dto.response.role.RoleInfo;
import com.nhn.model.entity.rolePermission.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    @Query("""
           SELECT
            new com.nhn.model.dto.response.permission.PermissionInfo(p.id, p.permissionNo, p.description)
           FROM Role r
            INNER JOIN RolePermission rp on r.id = rp.roleId
            INNER JOIN Permission p on p.id = rp.permissionId
           WHERE r.id = :roleId
        """)
    List<PermissionInfo> findPermissionsByRoleId(UUID roleId);

    @Query("SELECT rp.permissionId FROM RolePermission rp WHERE rp.roleId = :roleId")
    List<UUID> findPermissionIdsByRoleId(UUID roleId);

    @Query("""
           SELECT
           new com.nhn.model.dto.response.role.RoleInfo(r.id, r.name, r.roleNo)
           FROM Role r
            INNER JOIN AppUser au on r.id = au.role.Id
           WHERE au.id = :userId
        """)
    RoleInfo getRoleNoByUserId(UUID userId);

    boolean existsByRoleNo(String roleNo);

    Optional<Role> findByRoleNo(String roleNo);
}
