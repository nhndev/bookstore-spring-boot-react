package com.nhn.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nhn.service.user.AppUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final AppUserService appUserService;

    //	@GetMapping
    //	@PreAuthorize("hasAuthority('SUPER_ADMIN')")
    //	public ResponseEntity<ResponsePageDTO> findAll(
    //            @RequestParam(name = "keyword", defaultValue = "") final String keyword,
    //            @RequestParam(name = "page", defaultValue = "0") final int page, //page number
    //            @RequestParam(name = "limit", defaultValue = "20") final int limit, //page size
    //            @RequestParam(name = "orderBy", defaultValue = "email") final String orderBy, //database field
    //            @RequestParam(name = "sortBy", defaultValue = "asc") final String sortBy
    //	) {
    //
    //		final Sort sort = Sort.by(sortBy.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, orderBy);
    //
    //		final Pageable pageable = PageRequest.of(page, limit, sort);
    //		return ResponseEntity.ok().body(this.appUserService.findAll(keyword, pageable));
    //	}

    //	@PostMapping
    //	@Operation(summary = "Create a new user")
    //	@PreAuthorize("hasAuthority('SUPER_ADMIN')")
    //	public ResponseEntity<BaseResponse> create(final HttpServletRequest request, @Valid @RequestBody final UserCreateDTO userCreateDTO)
    //			throws MessagingException, IOException, NotFoundException {
    //		return ResponseEntity.ok().body(this.appUserService.createNewUser(userCreateDTO, request));
    //	}

    @PostMapping("/resend-email-verification/{email}")
    public ResponseEntity<?> resendEmailVerification(@PathVariable final String email) throws Exception {
        return ResponseEntity.ok()
                             .body(this.appUserService.resendEmailVerification(email));
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam final String verificationCode,
                                    @RequestParam final String email) {
        return ResponseEntity.ok()
                             .body(this.appUserService.verify(verificationCode,
                                                              email));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        return ResponseEntity.ok().body(this.appUserService.getProfile());
    }
    //
    //	@PutMapping("/profile")
    //	@Operation(summary = "Update lastname, firstname user")
    //	public ResponseEntity<?> updateProfile(@RequestBody final UserUpdateDTO userUpdateDTO) throws NotFoundException {
    //		final UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    //
    //		return ResponseEntity.ok().body(this.appUserService.updateProfile(userDetails.getUsername(), userUpdateDTO));
    //	}
    //
    //	@PutMapping("/change-password")
    //	@Operation(summary = "Change password")
    //	public ResponseEntity<?> changePassword(@Valid @RequestBody final UserChangePasswordDTO userChangePasswordDTO) throws NotFoundException {
    //		final UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    //
    //		return ResponseEntity.ok().body(this.appUserService.changePassword(userDetails.getUsername(), userChangePasswordDTO));
    //	}
    //
    //	@PostMapping("/forgot-password")
    //	public ResponseEntity<?> forgotPassword(@RequestBody final UserForgotPasswordDTO userForgotPasswordDTO)
    //			throws MessagingException, IOException, NotFoundException {
    //
    //        this.appUserService.forgotPassword(userForgotPasswordDTO);
    //
    //		final ObjectMapper mapper = new ObjectMapper();
    //		final JsonNode json = mapper.readTree("{\"message\":\"Send email reset password successfully\"}");
    //
    //		return ResponseEntity.ok().body(json);
    //
    //	}
    //
    //	@PutMapping("/reset-password")
    //	@Operation(summary = "Reset password")
    //	public ResponseEntity<?> resetPassword(@RequestBody final UserResetPasswordDTO userResetPasswordDTO) {
    //
    //		return ResponseEntity.ok().body(this.appUserService.resetPassword(userResetPasswordDTO));
    //
    //	}
}
