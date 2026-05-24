package com.nhn.service.user;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.nhn.constant.AppMsg;
import com.nhn.constant.SysRole;
import com.nhn.enums.TokenTypes;
import com.nhn.enums.UserStatusEnum;
import com.nhn.exception.FuncErrorException;
import com.nhn.mapstruct.UserMapping;
import com.nhn.model.dto.request.user.ResetPasswordRequest;
import com.nhn.model.dto.request.user.UserLoginRequest;
import com.nhn.model.dto.request.user.UserRegisterRequest;
import com.nhn.model.dto.response.BaseResponse;
import com.nhn.model.dto.response.permission.PermissionInfo;
import com.nhn.model.dto.response.user.AuthUserRoleInfo;
import com.nhn.model.dto.response.user.UserLoginResponse;
import com.nhn.model.dto.response.user.UserResponse;
import com.nhn.model.entity.rolePermission.Role;
import com.nhn.model.entity.user.AppUser;
import com.nhn.model.entity.user.AuthUser;
import com.nhn.properties.AppSetting;
import com.nhn.properties.JwtSetting;
import com.nhn.repository.AppUserRepository;
import com.nhn.repository.rolePermission.RoleRepository;
import com.nhn.service.MailService;
import com.nhn.util.CookieUtil;
import com.nhn.util.ErrorMsgUtil;
import com.nhn.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserService {
    private static final long VERIFICATION_EMAIL_COOLDOWN_MINUTES = 5;

    private final AuthenticationManager authManager;

    private final PasswordEncoder passwordEncoder;

    private final AppUserRepository appUserRepository;

    private final RoleRepository roleRepository;

    private final UserMapping userMapping;

    private final MailService mailService;

    private final JwtUtil jwtUtil;

    private final AppSetting appSetting;

    private final RefreshTokenService refreshTokenService;

    private final JwtSetting jwtSetting;

    @Transactional
    public BaseResponse register(final UserRegisterRequest request,
                                 final HttpServletRequest httpServletRequest) throws Exception {
        // get default role
        final Role role = this.roleRepository.findByRoleNo(SysRole.CUSTOMER)
                                             .orElseThrow(() -> new FuncErrorException(ErrorMsgUtil.createRoleNoNotExistsErrorResponse(SysRole.CUSTOMER)));
        // check if user with email already exists
        final String email = request.getEmail();
        if (this.appUserRepository.existsByEmail(email)) {
            throw new FuncErrorException(ErrorMsgUtil.createEmailExistsErrorResponse(email));
        }

        final String  password         = request.getPassword();
        final String  fullName         = request.getFullName();
        final String  verificationCode = RandomStringUtils.random(64, true, true);
        final AppUser appUser          = AppUser.builder().email(email)
                                                .password(this.passwordEncoder.encode(password))
                                                .verificationCode(verificationCode)
                                                .fullName(fullName).role(role)
                                                .status(UserStatusEnum.INACTIVE.getCode())
                                                .lastVerificationEmailSentAt(Date.from(Instant.now()))
                                                .build();
        this.appUserRepository.save(appUser);

        // send email to verify account
        this.sendEmailVerification(appUser);
        return BaseResponse.builder().data(AppMsg.FUNC_SUCCESS_MSG).build();
    }

    public BaseResponse<UserLoginResponse> login(final UserLoginRequest request,
                                                  final HttpServletResponse response) {
        final String email    = request.getEmail();
        final String password = request.getPassword();
        try {
            final Authentication authentication = this.authManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            final String accessToken  = this.jwtUtil.generateToken(email, TokenTypes.ACCESS_TOKEN);
            final String refreshToken = this.jwtUtil.generateToken(email, TokenTypes.REFRESH_TOKEN);

            // Get user ID for refresh token storage
            final AuthUser authUser = (AuthUser) authentication.getPrincipal();
            final UUID     userId   = authUser.getUser().getId();

            // Delete existing refresh tokens then create new one
            this.refreshTokenService.deleteByUserId(userId);
            this.refreshTokenService.createRefreshToken(userId, refreshToken,
                new Date(System.currentTimeMillis() + 1000 * this.jwtSetting.getExpirationRefreshToken()));

            // Set HTTP-only cookie
            final boolean        isSecure = this.appSetting.getOrigin().getWeb().startsWith("https");
            final ResponseCookie cookie   = CookieUtil.createRefreshTokenCookie(
                refreshToken, this.jwtSetting.getExpirationRefreshToken(), isSecure);
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return BaseResponse.<UserLoginResponse>builder()
                               .data(UserLoginResponse.builder()
                                                      .accessToken(accessToken)
                                                      .user(this.getCurrentUser())
                                                      .build())
                               .build();
        } catch (final BadCredentialsException ex) {
            throw new FuncErrorException(ErrorMsgUtil.createLoginEmailPasswordNotMatchErrorResponse());
        } catch (final DisabledException e) {
            throw new FuncErrorException(ErrorMsgUtil.createLoginUserInactiveErrorResponse());
        } catch (final Exception ex) {
            log.error("Login Error: ", ex);
            throw new FuncErrorException(ErrorMsgUtil.createLoginFailedErrorResponse());
        }
    }

    @Transactional
    public BaseResponse verify(final String verificationCode,
                               final String email) {
        final AppUser user = this.appUserRepository.findByEmail(email)
                                                   .orElseThrow(() -> new FuncErrorException(ErrorMsgUtil.createEmailNotExistsErrorResponse(email)));
        // check if user is already active
        if (user.getStatus() == UserStatusEnum.ACTIVE.getCode()) {
            throw new FuncErrorException(ErrorMsgUtil.createVerifyEmailAlreadyErrorResponse());
        }
        if (!StringUtils.equals(verificationCode, user.getVerificationCode())) {
            throw new FuncErrorException(ErrorMsgUtil.createVerifyEmailInvalidCodeErrorResponse());
        }
        user.setStatus(UserStatusEnum.ACTIVE.getCode());
        user.setVerificationCode(null);
        this.appUserRepository.save(user);

        return BaseResponse.builder().data(AppMsg.FUNC_SUCCESS_MSG).build();
    }

    public BaseResponse getProfile() {
        return BaseResponse.<UserResponse>builder().data(this.getCurrentUser())
                           .build();
    }

    @Transactional
    public BaseResponse resendEmailVerification(final String email) throws Exception {
        final AppUser user = this.appUserRepository.findByEmail(email)
                                                   .orElseThrow(() -> new FuncErrorException(ErrorMsgUtil.createEmailNotExistsErrorResponse(email)));
        if (user.getStatus() == UserStatusEnum.ACTIVE.getCode()) {
            throw new FuncErrorException(ErrorMsgUtil.createVerifyEmailAlreadyErrorResponse());
        }

        if (Objects.nonNull(user.getLastVerificationEmailSentAt())) {
            final Instant cooldownEnd = user.getLastVerificationEmailSentAt().toInstant()
                                            .plus(VERIFICATION_EMAIL_COOLDOWN_MINUTES, ChronoUnit.MINUTES);
            if (Instant.now().isBefore(cooldownEnd)) {
                throw new FuncErrorException(ErrorMsgUtil.createVerifyEmailRateLimitErrorResponse(VERIFICATION_EMAIL_COOLDOWN_MINUTES));
            }
        }

        final String verificationCode = RandomStringUtils.random(64, true, true);
        user.setVerificationCode(verificationCode);
        user.setLastVerificationEmailSentAt(Date.from(Instant.now()));

        this.appUserRepository.save(user);

        this.sendEmailVerification(user);

        return BaseResponse.builder().data(AppMsg.FUNC_SUCCESS_MSG).build();
    }

    @Transactional
    public BaseResponse forgotPassword(final String email) throws Exception {
        final Optional<AppUser> optUser = this.appUserRepository.findByEmail(email);
        if (optUser.isPresent()) {
            final AppUser user      = optUser.get();
            final String  resetCode = RandomStringUtils.random(64, true, true);
            user.setResetPasswordCode(resetCode);
            user.setResetPasswordCodeExpiresAt(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)));
            this.appUserRepository.save(user);
            this.sendResetPasswordEmail(user);
        }
        // Always return success — prevent email enumeration
        return BaseResponse.builder().data(AppMsg.FUNC_SUCCESS_MSG).build();
    }

    @Transactional
    public BaseResponse resetPassword(final ResetPasswordRequest request) {
        final AppUser user = this.appUserRepository.findByEmail(request.getEmail())
                                                   .orElseThrow(() -> new FuncErrorException(ErrorMsgUtil.createEmailNotExistsErrorResponse(request.getEmail())));
        if (!StringUtils.equals(request.getResetPasswordCode(), user.getResetPasswordCode())) {
            throw new FuncErrorException(ErrorMsgUtil.createResetCodeInvalidErrorResponse());
        }
        if (user.getResetPasswordCodeExpiresAt() == null || user.getResetPasswordCodeExpiresAt().before(new Date())) {
            throw new FuncErrorException(ErrorMsgUtil.createResetCodeExpiredErrorResponse());
        }
        user.setPassword(this.passwordEncoder.encode(request.getNewPassword()));
        user.setResetPasswordCode(null);
        user.setResetPasswordCodeExpiresAt(null);
        this.appUserRepository.save(user);

        // Invalidate all refresh tokens
        this.refreshTokenService.deleteByUserId(user.getId());

        return BaseResponse.builder().data(AppMsg.FUNC_SUCCESS_MSG).build();
    }

    @Transactional
    private UserResponse getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext()
                                                                   .getAuthentication();
        if (Objects.isNull(authentication)) {
            throw new FuncErrorException(ErrorMsgUtil.createUnauthorizedExceptionResponse());
        }
        final AuthUser         authUser = (AuthUser) authentication.getPrincipal();
        final AuthUserRoleInfo user     = authUser.getUser();
        if (StringUtils.isBlank(user.getAvatarUrl())) {
            // TODO
            user.setAvatarUrl("https://media.licdn.com/dms/image/v2/D5603AQGiJ7mWtehArg/profile-displayphoto-shrink_200_200/profile-displayphoto-shrink_200_200/0/1722182056966?e=2147483647&v=beta&t=lU3RO_h8B8-v01nLf9GaeeBSGmNdM-FWs3oTSHBX3D8");
        }
        final UUID                 roleId       = user.getRoleId();
        final List<PermissionInfo> permissions  = this.roleRepository.findPermissionsByRoleId(roleId);
        final UserResponse         userResponse = this.userMapping.toUserResponse(user);
        userResponse.setRole(user.getRoleName());
        userResponse.setPermissions(permissions);
        return userResponse;
    }

    private void sendEmailVerification(final AppUser user) throws Exception {
        final String        email         = user.getEmail();
        final UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(this.appSetting.getOrigin()
                                                                                            .getWeb()
                                                                             + "/xac-minh-email")
                                                                .queryParam(AppUser.Fields.verificationCode,
                                                                            user.getVerificationCode())
                                                                .queryParam(AppUser.Fields.email,
                                                                            email)
                                                                .build();
        log.info("verifyURL: {}", uriComponents);

        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(AppUser.Fields.fullName, user.getFullName());
        paramMap.put(AppUser.Fields.email, email);
        paramMap.put("verifyURL", uriComponents.toString());

        this.mailService.sendTemplateMail(email,
                                          "Xác nhận địa chỉ email của bạn",
                                          paramMap, "EmailVerification.vm");
    }

    private void sendResetPasswordEmail(final AppUser user) throws Exception {
        final String        email         = user.getEmail();
        final UriComponents uriComponents = UriComponentsBuilder
            .fromHttpUrl(this.appSetting.getOrigin().getWeb() + "/dat-lai-mat-khau")
            .queryParam("resetPasswordCode", user.getResetPasswordCode())
            .queryParam(AppUser.Fields.email, email)
            .build();

        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(AppUser.Fields.fullName, user.getFullName());
        paramMap.put("resetURL", uriComponents.toString());

        this.mailService.sendTemplateMail(email,
                                          "Đặt lại mật khẩu BookStore",
                                          paramMap, "EmailResetPassword.vm");
    }
}
