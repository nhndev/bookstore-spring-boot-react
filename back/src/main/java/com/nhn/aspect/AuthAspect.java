package com.nhn.aspect;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.nhn.annotation.SysAuthorize;
import com.nhn.exception.ForbiddenException;
import com.nhn.model.dto.response.permission.PermissionInfo;
import com.nhn.model.dto.response.user.AuthUserRoleInfo;
import com.nhn.model.entity.user.AuthUser;
import com.nhn.repository.rolePermission.RoleRepository;
import com.nhn.util.AuthUtil;
import com.nhn.util.ErrorMsgUtil;
import com.nhn.util.RedisCache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Order(1)
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthAspect {
    private final RedisCache redisCache;

    private final RoleRepository roleRepository;

    @Around("@annotation(sysAuthorize)")
    public Object aroundControllers(final ProceedingJoinPoint joinPoint,
                                    final SysAuthorize sysAuthorize) throws Throwable {
        if (Objects.isNull(sysAuthorize)) {
            return joinPoint.proceed();
        }

        final Authentication authentication = SecurityContextHolder.getContext()
                                                                   .getAuthentication();

        final AuthUser authUser = (AuthUser) authentication.getPrincipal();

        final Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) authentication.getAuthorities();
        final AuthUserRoleInfo                   user        = authUser.getUser();
        final UUID                             roleId      = user.getRoleId();
        if (Objects.isNull(roleId)) {
            return false;
        }

        final String   requiredRole       = sysAuthorize.role();
        final String[] requiredPermission = sysAuthorize.permissions();

        final String key = AuthUtil.getUserPermissionRedisKey(roleId);

        if (this.redisCache.hasKey(key)) {
            final List<String> rolePermissions = this.redisCache.getCacheObject(key);
            if (!this.hasPermission(authorities, requiredRole, rolePermissions,
                                    requiredPermission)) {
                throw new ForbiddenException(ErrorMsgUtil.createForbiddenExceptionResponse());
            }
        }


        final List<PermissionInfo> permissionInfoList = this.roleRepository.findPermissionsByRoleId(roleId);
        final List<String>         permissions        = permissionInfoList.stream()
                                                                          .map(PermissionInfo::getPermissionNo)
                                                                          .toList();
        this.redisCache.setCacheObject(key, permissions);
        if (!this.hasPermission(authorities, requiredRole, permissions,
                                requiredPermission)) {
            throw new ForbiddenException(ErrorMsgUtil.createForbiddenExceptionResponse());
        }

        return joinPoint.proceed();
    }

    private boolean hasPermission(final Collection<SimpleGrantedAuthority> authorities,
                                  final String requiredRole,
                                  final List<String> rolePermissions,
                                  final String[] requiredPermission) {
        log.info("authorities: {}, requiredRole: {}", authorities,
                 requiredRole);
        log.info("rolePermissions: {}, requiredPermission: {}", rolePermissions,
                 requiredPermission);
        return this.hasRole(authorities, requiredRole)
               || this.hasPermission(rolePermissions, requiredPermission);
    }

    private boolean hasRole(final Collection<SimpleGrantedAuthority> authorities,
                            final String requiredRole) {
        return authorities.contains(new SimpleGrantedAuthority(requiredRole));
    }

    private boolean hasPermission(final List<String> rolePermissions,
                                  final String[] requiredPermission) {
        for (final String permission : requiredPermission) {
            if (rolePermissions.contains(permission)) {
                return true;
            }
        }
        return false;
    }
}
