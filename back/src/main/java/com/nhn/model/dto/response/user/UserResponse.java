package com.nhn.model.dto.response.user;

import java.util.List;
import java.util.UUID;

import com.nhn.model.dto.response.permission.PermissionInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private UUID id;

    private String email;

    private String fullName;

    private String phoneNumber;

    private String avatarUrl;

    private String role;

    private List<PermissionInfo> permissions;
}
