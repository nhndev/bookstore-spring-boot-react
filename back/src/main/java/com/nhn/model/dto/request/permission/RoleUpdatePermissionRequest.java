package com.nhn.model.dto.request.permission;

import java.util.List;
import java.util.UUID;

import com.nhn.annotation.validation.Required;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleUpdatePermissionRequest {
    @Required(message = "roleId is required")
    private UUID roleId;

    @Required(message = "permissionIds is required")
    private List<UUID> permissionIds;
}
