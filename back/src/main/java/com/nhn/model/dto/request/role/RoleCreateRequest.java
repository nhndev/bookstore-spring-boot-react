package com.nhn.model.dto.request.role;

import com.nhn.annotation.validation.Required;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleCreateRequest {
    @Required(message = "roleNo is required")
    private String roleNo;

    @Required(message = "name is required")
	private String name;
}
