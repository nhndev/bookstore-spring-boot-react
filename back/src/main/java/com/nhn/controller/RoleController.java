package com.nhn.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nhn.annotation.SysAuthorize;
import com.nhn.constant.SysRole;
import com.nhn.model.dto.request.permission.RoleUpdatePermissionRequest;
import com.nhn.model.dto.request.role.RoleCreateRequest;
import com.nhn.model.dto.response.BaseResponse;
import com.nhn.model.entity.rolePermission.Role;
import com.nhn.service.role.RoleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    @SysAuthorize(role = SysRole.SUPER_ADMIN)
    public ResponseEntity<?> findAll(@SortDefault(sort = Role.Fields.roleNo, direction = Sort.Direction.ASC) final Pageable pageable) {
        return ResponseEntity.ok().body(this.roleService.findAll(pageable));

    }

    @GetMapping("/{id}")
    @SysAuthorize(role = SysRole.SUPER_ADMIN)
    public ResponseEntity<BaseResponse> findById(@PathVariable final UUID id) {
        return ResponseEntity.ok().body(this.roleService.findById(id));
    }

    @PostMapping
    @SysAuthorize(role = SysRole.SUPER_ADMIN)
    public ResponseEntity<BaseResponse> create(@Valid @RequestBody final RoleCreateRequest request) {
        return ResponseEntity.ok().body(this.roleService.createRole(request));
    }

    @PutMapping("/permission")
    @SysAuthorize(role = SysRole.SUPER_ADMIN)
    public ResponseEntity<BaseResponse> updatePermission(@Valid @RequestBody final RoleUpdatePermissionRequest request) {
        return ResponseEntity.ok()
                             .body(this.roleService.updatePermission(request));
    }
}
