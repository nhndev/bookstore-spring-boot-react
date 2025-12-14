package com.nhn.util;

import java.util.UUID;

import com.nhn.constant.AuthConstant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthUtil {
    public static String getUserPermissionRedisKey(final UUID roleId) {
        return AuthConstant.USER_PERMISSIONS_PREFIX_KEY + roleId;
    }
}
