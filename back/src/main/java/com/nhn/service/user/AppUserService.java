package com.nhn.service.user;

import java.util.*;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
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
import com.nhn.repository.AppUserRepository;
import com.nhn.repository.rolePermission.RoleRepository;
import com.nhn.service.MailService;
import com.nhn.util.ErrorMsgUtil;
import com.nhn.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserService {
    private final AuthenticationManager authManager;

    private final PasswordEncoder passwordEncoder;

    private final AppUserRepository appUserRepository;

    private final RoleRepository roleRepository;

    private final UserMapping userMapping;

    private final MailService mailService;

    private final JwtUtil jwtUtil;

    private final AppSetting appSetting;

    private RedisTemplate redisTemplate;

    //    public ResponsePageDTO findAll(final String keyword, final Pageable pageable) {
    //
    //        final Page<AppUser> userPage = this.appUserRepository.findAll(keyword, pageable);
    //
    //        final List<UserBasicDTO> userBasicDTOS = new ArrayList<>();
    //        for (final AppUser appUser : userPage.getContent()) {
    //            userBasicDTOS.add(this.userMapper.fromEntityToBasic(appUser));
    //        }
    //
    //        return ResponsePageDTO.builder().data(userBasicDTOS)
    //                              .limit(userPage.getSize())
    //                              .currentPage(userPage.getNumber())
    //                              .totalItems(userPage.getTotalElements())
    //                              .totalPages(userPage.getTotalPages()).build();
    //    }
    //
    //    public BaseResponse findByEmail(final String email) throws NotFoundException {
    //
    //        final AppUser user = this.appUserRepository.findByEmail(email)
    //                                        .orElseThrow(() -> new NotFoundException("User not found: "
    //                                                                                 + email));
    //
    //        return BaseResponse.builder()
    //                               .data(this.userMapper.fromEntityToBasic(user))
    //                               .isSuccess(true).build();
    //    }
    //
    //    @Transactional
    //    public BaseResponse createNewUser(final UserCreateDTO userCreateDTO,
    //                                      final HttpServletRequest request) throws NotFoundException, MessagingException, IOException {
    //        final Role role = this.roleRepository.findById(userCreateDTO.getRoleId())
    //                                  .orElseThrow(() -> new NotFoundException("Not found role with id: "
    //                                                                           + userCreateDTO.getRoleId()));
    //
    //        if (this.appUserRepository.existsByEmail(userCreateDTO.getEmail())) {
    //            throw new BadRequestException("User with this email already exists");
    //        }
    //
    //        final AppUser appUser = this.userMapper.fromCreateToEntity(userCreateDTO);
    //
    //        final String randomCode     = RandomString.make(64);
    //        final String randomPassword = RandomString.make(8);
    //
    //        appUser.setVerificationCode(randomCode);
    //        appUser.setRole(role);
    //        appUser.setPassword(this.passwordEncoder.encode(randomPassword));
    //
    //        final String verifyURL = request.getRequestURL().toString()
    //                                  .replace(request.getServletPath(), "")
    //                           + "/api/v1/users/verify?code=" + randomCode;
    //
    //        final Map<String, String> properties = new HashMap<>();
    //
    //        properties.put("lastname", appUser.getLastname());
    //        properties.put("firstname", appUser.getFirstname());
    //        properties.put("email", appUser.getEmail());
    //        properties.put("password", randomPassword);
    //        properties.put("link", verifyURL);
    //
    //        final UserBasicDTO userBasicDTO = this.userMapper.fromEntityToBasic(this.appUserRepository.save(appUser));
    //
    //        this.sendEmailService.sendVerifyAccountCreate(properties,
    //                                                 appUser.getEmail());
    //
    //        return BaseResponse.builder().data(userBasicDTO).isSuccess(true)
    //                               .build();
    //    }

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
        final String  verificationCode = RandomStringUtils.random(64, true,
                                                                  true);
        final AppUser appUser          = AppUser.builder().email(email)
                                                .password(this.passwordEncoder.encode(password))
                                                .verificationCode(verificationCode)
                                                .fullName(fullName).role(role)
                                                .status(UserStatusEnum.INACTIVE.getCode())
                                                .build();
        this.appUserRepository.save(appUser);

        // send email to verify account
        this.sendEmailVerification(appUser);
        return BaseResponse.builder().data(AppMsg.FUNC_SUCCESS_MSG).build();
    }

    public BaseResponse<UserLoginResponse> login(final UserLoginRequest request) {
        final String email    = request.getEmail();
        final String password = request.getPassword();
        try {
            final Authentication authentication = this.authManager.authenticate(new UsernamePasswordAuthenticationToken(email,
                                                                                                                        password));
            SecurityContextHolder.getContext()
                                 .setAuthentication(authentication);

            final String accessToken = this.jwtUtil.generateToken(email,
                                                                  TokenTypes.ACCESS_TOKEN);
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
        // check if user is already active
        if (user.getStatus() == UserStatusEnum.ACTIVE.getCode()) {
            throw new FuncErrorException(ErrorMsgUtil.createVerifyEmailAlreadyErrorResponse());
        }

        final String verificationCode = RandomStringUtils.random(64, true,
                                                                 true);
        user.setVerificationCode(verificationCode);

        this.appUserRepository.save(user);

        // send email to verify account
        this.sendEmailVerification(user);

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
                                                                                            .getApi()
                                                                             + "/api/v1/users/verify")
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


    //
    //    public BaseResponse changePassword(final String email,
    //                                       final UserChangePasswordDTO userChangePasswordDTO) throws NotFoundException {
    //        final AppUser user = this.appUserRepository.findByEmail(email)
    //                                        .orElseThrow(() -> new NotFoundException("User not found: "
    //                                                                                 + email));
    //
    //        if (this.passwordEncoder.matches(userChangePasswordDTO.getCurrentPassword(),
    //                                    user.getPassword())) {
    //            user.setPassword(this.passwordEncoder.encode(userChangePasswordDTO.getNewPassword()));
    //
    //            return BaseResponse.builder()
    //                                   .data(this.userMapper.fromEntityToBasic(this.appUserRepository.save(user)))
    //                                   .isSuccess(true).build();
    //
    //        } else {
    //            throw new BadRequestException("Current password is invalid");
    //        }
    //    }
    //
    //    @Transactional
    //    public void forgotPassword(final UserForgotPasswordDTO forgotPasswordDTO) throws MessagingException, IOException, NotFoundException {
    //        final String email = forgotPasswordDTO.getEmail();
    //
    //        final AppUser user = this.appUserRepository.findByEmail(email)
    //                                        .orElseThrow(() -> new NotFoundException("User not found: "
    //                                                                                 + email));
    //
    //        final String randomCode = RandomString.make(64);
    //
    //        user.setResetPasswordCode(randomCode);
    //        this.appUserRepository.save(user);
    //
    //        final String              link       = this.rootUrl
    //                                         + "/swagger-ui/index.html#/user-controller/resetPassword?code="
    //                                         + randomCode;
    //        final Map<String, String> properties = new HashMap<>();
    //
    //        properties.put("lastname", user.getLastname());
    //        properties.put("firstname", user.getFirstname());
    //        properties.put("link", link);
    //
    //        this.sendEmailService.sendForgotPasswordEmail(properties, user.getEmail());
    //
    //    }
    //
    //    public BaseResponse resetPassword(final UserResetPasswordDTO userResetPasswordDTO) {
    //        final AppUser user = this.appUserRepository.findByResetPasswordCode(userResetPasswordDTO.getResetPasswordCode())
    //                                        .orElseThrow(() -> new BadRequestException("Code is invalid"));
    //
    //        user.setPassword(this.passwordEncoder.encode(userResetPasswordDTO.getPassword()));
    //        user.setResetPasswordCode(null);
    //
    //        return BaseResponse.builder()
    //                               .data(this.userMapper.fromEntityToBasic(this.appUserRepository.save(user)))
    //                               .isSuccess(true).build();
    //    }
    //
    //    public BaseResponse updateProfile(final String email,
    //                                      final UserUpdateDTO userUpdateDTO) throws NotFoundException {
    //
    //        final AppUser user = this.appUserRepository.findByEmail(email)
    //                                        .orElseThrow(() -> new NotFoundException("User not found: "
    //                                                                                 + email));
    //
    //        user.setFirstname(userUpdateDTO.getFirstname());
    //        user.setLastname(userUpdateDTO.getLastname());
    //
    //        return BaseResponse.builder()
    //                               .data(this.userMapper.fromEntityToBasic(this.appUserRepository.save(user)))
    //                               .isSuccess(true).build();
    //    }
}
