package com.nhn.model.dto.response.role;

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
public class RoleDetailInfo {
    private UUID id;
    private String name;
    private String roleNo;
    private List<PermissionInfo> permissions;
}
