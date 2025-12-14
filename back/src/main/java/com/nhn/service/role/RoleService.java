package com.nhn.service.role;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhn.constant.AppMsg;
import com.nhn.exception.FuncErrorException;
import com.nhn.model.dto.request.permission.RoleUpdatePermissionRequest;
import com.nhn.model.dto.request.role.RoleCreateRequest;
import com.nhn.model.dto.response.BaseResponse;
import com.nhn.model.dto.response.permission.PermissionInfo;
import com.nhn.model.dto.response.role.RoleDetailInfo;
import com.nhn.model.dto.response.role.RoleInfo;
import com.nhn.model.entity.rolePermission.Role;
import com.nhn.model.entity.rolePermission.RolePermission;
import com.nhn.repository.rolePermission.PermissionRepository;
import com.nhn.repository.rolePermission.RolePermissionRepository;
import com.nhn.repository.rolePermission.RoleRepository;
import com.nhn.util.AuthUtil;
import com.nhn.util.ErrorMsgUtil;
import com.nhn.util.RedisCache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {
    private final RoleRepository roleRepository;

    private final PermissionRepository permissionRepository;

    private final RolePermissionRepository rolePermissionRepository;

    private final RedisCache redisCache;

    public BaseResponse findAll(final Pageable pageable) {
        final Page<Role> rolePage = this.roleRepository.findAll(pageable);

        final List<RoleInfo> result = rolePage.getContent().stream()
                                              .map(this::buildRoleInfo)
                                              .toList();

        return BaseResponse.builder().data(result).build();
    }

    @Transactional
    public BaseResponse createRole(final RoleCreateRequest request) {
        final String roleNo = request.getRoleNo();
        if (this.roleRepository.existsByRoleNo(roleNo)) {
            throw new FuncErrorException(ErrorMsgUtil.createRoleNoExistsErrorResponse(roleNo));
        }

        final Role role      = Role.builder().roleNo(roleNo)
                                   .name(request.getName()).build();
        final Role savedRole = this.roleRepository.save(role);

        return BaseResponse.builder().data(this.buildRoleInfo(savedRole))
                           .build();
    }

    public BaseResponse findById(final UUID id) {
        final Role role = this.roleRepository.findById(id)
                                             .orElseThrow(() -> new FuncErrorException(ErrorMsgUtil.createRoleNotFoundErrorResponse()));

        final List<PermissionInfo> permissions = this.roleRepository.findPermissionsByRoleId(id);
        return BaseResponse.builder()
                           .data(RoleDetailInfo.builder().id(id)
                                               .name(role.getName())
                                               .roleNo(role.getRoleNo())
                                               .permissions(permissions)
                                               .build())
                           .build();
    }

    @Transactional
    public BaseResponse updatePermission(final RoleUpdatePermissionRequest request) {
        final UUID roleId = request.getRoleId();
        if (!this.roleRepository.existsById(roleId)) {
            throw new FuncErrorException(ErrorMsgUtil.createRoleNotFoundErrorResponse());
        }

        // validate permission ids
        final List<UUID> permissionsRequest = request.getPermissionIds();
        final List<UUID> newPermissionIds   = this.permissionRepository.findPermissionIdsIn(permissionsRequest);
        if (!ArrayUtils.isSameLength(permissionsRequest.toArray(),
                                     newPermissionIds.toArray())) {
            log.error("[Update permission] invalid permissions: {}",
                      permissionsRequest);
            throw new FuncErrorException(ErrorMsgUtil.createRoleUpdatePermissionInvalidRequestErrorResponse());
        }

        final List<UUID> oldPermissionIds = this.roleRepository.findPermissionIdsByRoleId(roleId);
        log.info("[Update permission] roleId: {}, oldPermissionIds: {}, newPermissionIds: {}",
                 roleId, oldPermissionIds, newPermissionIds);

        final List<UUID> deletePermissionIds = oldPermissionIds.stream()
                                                               .filter(permissionId -> !newPermissionIds.contains(permissionId))
                                                               .toList();
        final List<UUID> addPermissionIds    = newPermissionIds.stream()
                                                               .filter(permissionId -> !oldPermissionIds.contains(permissionId))
                                                               .toList();
        log.info("[Update permission] roleId: {}, deletePermissionIds: {}, addPermissionIds: {}",
                 roleId, deletePermissionIds, addPermissionIds);

        boolean isUpdate = false;
        // delete old permissions
        if (CollectionUtils.isNotEmpty(deletePermissionIds)) {
            this.rolePermissionRepository.deleteByRoleIdAndPermissionIds(roleId,
                                                                         deletePermissionIds);
            isUpdate = true;
        }

        // add new permissions
        if (CollectionUtils.isNotEmpty(addPermissionIds)) {
            final List<RolePermission> newRolePermissions = addPermissionIds.stream()
                                                                            .map(permissionId -> RolePermission.builder()
                                                                                                               .roleId(roleId)
                                                                                                               .permissionId(permissionId)
                                                                                                               .build())
                                                                            .collect(Collectors.toList());
            this.rolePermissionRepository.saveAll(newRolePermissions);
            isUpdate = true;
        }

        if (!isUpdate) {
            this.redisCache.deleteObject(AuthUtil.getUserPermissionRedisKey(roleId));
        }

        return BaseResponse.builder().data(AppMsg.FUNC_SUCCESS_MSG).build();

    }

    private RoleInfo buildRoleInfo(final Role entity) {
        return RoleInfo.builder().id(entity.getId()).name(entity.getName())
                       .build();
    }
}
