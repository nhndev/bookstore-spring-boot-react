package com.nhn.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nhn.enums.TokenTypes;
import com.nhn.exception.FuncErrorException;
import com.nhn.model.dto.request.user.*;
import com.nhn.model.dto.response.BaseResponse;
import com.nhn.model.dto.response.user.UserLoginResponse;
import com.nhn.model.entity.user.RefreshToken;
import com.nhn.properties.AppSetting;
import com.nhn.properties.JwtSetting;
import com.nhn.service.user.AppUserService;
import com.nhn.service.user.RefreshTokenService;
import com.nhn.util.CookieUtil;
import com.nhn.util.ErrorMsgUtil;
import com.nhn.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AppUserService      appUserService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil             jwtUtil;
    private final JwtSetting          jwtSetting;
    private final AppSetting          appSetting;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse> register(final HttpServletRequest httpServletRequest,
                                                 @Valid @RequestBody final UserRegisterRequest request) throws Exception {
        return ResponseEntity.ok()
                             .body(this.appUserService.register(request, httpServletRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<UserLoginResponse>> login(@Valid @RequestBody final UserLoginRequest request,
                                                                  final HttpServletResponse response) {
        return ResponseEntity.ok().body(this.appUserService.login(request, response));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<BaseResponse<UserLoginResponse>> refreshToken(final HttpServletRequest request,
                                                                         final HttpServletResponse response) {
        final String token = CookieUtil.extractRefreshToken(request);
        if (StringUtils.isBlank(token)) {
            throw new FuncErrorException(ErrorMsgUtil.createRefreshTokenInvalidErrorResponse());
        }
        final RefreshToken refreshToken = this.refreshTokenService.validateRefreshToken(token);
        final String       email        = this.jwtUtil.getUsername(token);
        final String       accessToken  = this.jwtUtil.generateToken(email, TokenTypes.ACCESS_TOKEN);

        return ResponseEntity.ok()
                             .body(BaseResponse.<UserLoginResponse>builder()
                                               .data(UserLoginResponse.builder()
                                                                      .accessToken(accessToken)
                                                                      .build())
                                               .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse> logout(final HttpServletRequest request,
                                               final HttpServletResponse response) {
        final String token = CookieUtil.extractRefreshToken(request);
        if (StringUtils.isNotBlank(token)) {
            try {
                final RefreshToken refreshToken = this.refreshTokenService.validateRefreshToken(token);
                this.refreshTokenService.deleteByUserId(refreshToken.getUserId());
            } catch (final Exception ignored) {
                // Token already invalid — still clear cookie
            }
        }
        final boolean        isSecure = this.appSetting.getOrigin().getWeb().startsWith("https");
        final ResponseCookie cookie   = CookieUtil.clearRefreshTokenCookie(isSecure);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().body(BaseResponse.builder().data("OK").build());
    }

    @GetMapping("/verify-email")
    public ResponseEntity<BaseResponse> verifyEmail(@RequestParam final String verificationCode,
                                                    @RequestParam final String email) {
        return ResponseEntity.ok().body(this.appUserService.verify(verificationCode, email));
    }

    @PostMapping("/resend-email-verification")
    public ResponseEntity<BaseResponse> resendEmailVerification(@Valid @RequestBody final ResendEmailRequest request) throws Exception {
        return ResponseEntity.ok().body(this.appUserService.resendEmailVerification(request.getEmail()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<BaseResponse> forgotPassword(@Valid @RequestBody final ForgotPasswordRequest request) throws Exception {
        return ResponseEntity.ok().body(this.appUserService.forgotPassword(request.getEmail()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<BaseResponse> resetPassword(@Valid @RequestBody final ResetPasswordRequest request) {
        return ResponseEntity.ok().body(this.appUserService.resetPassword(request));
    }
}
