package com.nhn.repository.rolePermission;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nhn.model.entity.rolePermission.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    @Query("SELECT p.id FROM Permission p WHERE p.id IN (:ids)")
    List<UUID> findPermissionIdsIn(List<UUID> ids);
}
