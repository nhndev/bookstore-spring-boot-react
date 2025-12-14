package com.nhn.model.dto.request.user;

import com.nhn.annotation.validation.MaxLength;
import com.nhn.annotation.validation.Required;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserLoginRequest {
    @Required(message = "Email là trường bắt buộc")
    @Email(message = "Email không đúng định dạng")
    @MaxLength(value = 50, message = "Vui lòng nhập email không quá 50 ký tự")
    private String email;

    @Required(message = "Mật khẩu là trường bắt buộc")
    private String password;
}
