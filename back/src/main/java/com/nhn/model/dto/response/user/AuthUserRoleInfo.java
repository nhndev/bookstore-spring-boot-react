package com.nhn.model.dto.response.user;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUserRoleInfo {
    private UUID id;

    private String fullName;

    private String email;

    private String phoneNumber;

    private String password;

    private String avatarUrl;

    private Integer status;

    private UUID roleId;

    private String roleName;

    private String roleNo;
}
