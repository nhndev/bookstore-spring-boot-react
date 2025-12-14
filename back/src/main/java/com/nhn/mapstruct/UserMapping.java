package com.nhn.mapstruct;

import org.mapstruct.Mapper;

import com.nhn.model.dto.response.user.AuthUserRoleInfo;
import com.nhn.model.dto.response.user.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapping {
    UserResponse toUserResponse(AuthUserRoleInfo source);
}
