package com.nhn.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhn.annotation.SysAuthorize;
import com.nhn.constant.SysRole;
import com.nhn.model.dto.response.PaginationResponse;
import com.nhn.model.entity.rolePermission.Permission;
import com.nhn.service.permission.PermissionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @GetMapping
    @SysAuthorize(role = SysRole.SUPER_ADMIN)
    public ResponseEntity<PaginationResponse<Permission>> findAll(final Pageable pageable){
        return ResponseEntity.ok().body(this.permissionService.findAll(pageable));
    }
}
