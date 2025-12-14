package com.nhn.model.dto.response.role;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleInfo {
    private UUID id;
    private String name;
    private String roleNo;
}
