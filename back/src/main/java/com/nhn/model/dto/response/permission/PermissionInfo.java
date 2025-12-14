package com.nhn.model.dto.response.permission;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionInfo {
    private UUID id;

    private String permissionNo;

    private String description;
}
