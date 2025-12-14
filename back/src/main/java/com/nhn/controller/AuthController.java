package com.nhn.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhn.model.dto.request.user.UserLoginRequest;
import com.nhn.model.dto.request.user.UserRegisterRequest;
import com.nhn.model.dto.response.BaseResponse;
import com.nhn.model.dto.response.user.UserLoginResponse;
import com.nhn.service.user.AppUserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AppUserService appUserService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse> register(final HttpServletRequest httpServletRequest,
                                                 @Valid @RequestBody final UserRegisterRequest request) throws Exception {
        return ResponseEntity.ok()
                             .body(this.appUserService.register(request,
                                                                httpServletRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<UserLoginResponse>> login(@Valid @RequestBody final UserLoginRequest request) {
        return ResponseEntity.ok().body(this.appUserService.login(request));
    }

    //	@PostMapping("/refresh-token")
    //	public ResponseEntity<?> refreshToken(@Valid @RequestBody final UserRefreshTokenDTO refreshTokenDTO) throws NotFoundException {
    //		return ResponseEntity.ok().body(this.appUserService.refreshToken(refreshTokenDTO));
    //	}
    //
    //	@GetMapping("/logout")
    //	public ResponseEntity<?> logout(final HttpServletRequest httpServletRequest) {
    //		return ResponseEntity.ok().body(this.appUserService.logout(httpServletRequest));
    //	}
}
