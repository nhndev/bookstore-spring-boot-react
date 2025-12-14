package com.nhn.service.permission;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nhn.model.dto.response.PaginationResponse;
import com.nhn.model.entity.rolePermission.Permission;
import com.nhn.repository.rolePermission.PermissionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PaginationResponse<Permission> findAll(final Pageable pageable) {
        final Page<Permission> permissionPage = this.permissionRepository.findAll(pageable);
        return PaginationResponse.<Permission>builder()
                                 .data(permissionPage.getContent())
                                 .totalPages(permissionPage.getTotalPages())
                                 .build();
    }
}
