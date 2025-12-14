package com.nhn.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.nhn.model.dto.response.user.AuthUserRoleInfo;

@Mapper
public interface UserMapper {
    AuthUserRoleInfo getUserRoleInfoByEmail(String email);
}
