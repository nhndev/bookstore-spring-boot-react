package com.nhn.model.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String resetPasswordCode;

    @NotBlank
    @Size(min = 6)
    private String newPassword;
}
