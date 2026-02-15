package com.nhn.util;

import java.text.MessageFormat;

import org.springframework.http.HttpStatus;

import com.nhn.constant.ErrorCdMsg;
import com.nhn.model.dto.response.BaseErrorResponse;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorMsgUtil {
    public static BaseErrorResponse createUnauthorizedExceptionResponse() {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.UNAUTHORIZED.name())
                                .errorMessage(ErrorCdMsg.FUNC_UNAUTHORIZED_ERR_MSG)
                                .build();
    }

    public static BaseErrorResponse createForbiddenExceptionResponse() {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.FORBIDDEN.name())
                                .errorMessage(ErrorCdMsg.FUNC_FORBIDDEN_ERR_MSG)
                                .build();
    }

    public static BaseErrorResponse createCloudinaryUploadErrorResponse() {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.BAD_REQUEST.name())
                                .errorMessage(ErrorCdMsg.CLOUDINARY_UPLOAD_ERR_MSG)
                                .build();
    }

    public static BaseErrorResponse createCloudinaryDeleteErrorResponse() {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.BAD_REQUEST.name())
                                .errorMessage(ErrorCdMsg.CLOUDINARY_DELETE_ERR_MSG)
                                .build();
    }

    public static BaseErrorResponse createValidationImageExtensionErrorResponse(final String extension) {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.BAD_REQUEST.name())
                                .errorMessage(MessageFormat.format(ErrorCdMsg.VALIDATION_IMAGE_EXTENSION_ERR_MSG,
                                                                   extension))
                                .build();
    }

    public static BaseErrorResponse createValidationImageSizeErrorResponse(final long size) {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.BAD_REQUEST.name())
                                .errorMessage(MessageFormat.format(ErrorCdMsg.VALIDATION_IMAGE_SIZE_ERR_MSG,
                                                                   size))
                                .build();
    }

    public static BaseErrorResponse createMaxUploadSizeExceededExceptionErrorResponse() {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.BAD_REQUEST.name())
                                .errorMessage(MessageFormat.format(ErrorCdMsg.VALIDATION_IMAGE_SIZE_ERR_MSG,
                                                                   FileUploadUtil.DEFAULT_MAX_SIZE /
                                                                                                             1024 /
                                                                                                             1024))
                                .build();
    }

    public static BaseErrorResponse createInternalServerErrorResponse() {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.name())
                                .errorMessage(ErrorCdMsg.INTERNAL_SERVER_ERR_MSG)
                                .build();
    }

    public static BaseErrorResponse createLoginFailedErrorResponse() {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.NOT_FOUND.name())
                                .errorMessage(ErrorCdMsg.FUNC_LOGIN_FAILED_ERR_MSG)
                                .build();
    }

    public static BaseErrorResponse createLoginEmailPasswordNotMatchErrorResponse() {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.BAD_REQUEST.name())
                                .errorMessage(ErrorCdMsg.FUNC_LOGIN_EMAIL_PASSWORD_NOT_MATCH_ERR_MSG)
                                .build();
    }

    public static BaseErrorResponse createLoginUserInactiveErrorResponse() {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.BAD_REQUEST.name())
                                .errorMessage(ErrorCdMsg.FUNC_LOGIN_USER_INACTIVE_ERR_MSG)
                                .build();
    }

    public static BaseErrorResponse createVerifyEmailAlreadyErrorResponse() {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.BAD_REQUEST.name())
                                .errorMessage(ErrorCdMsg.FUNC_VERIFY_EMAIL_ALREADY_ERR_MSG)
                                .build();
    }

    public static BaseErrorResponse createVerifyEmailInvalidCodeErrorResponse() {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.BAD_REQUEST.name())
                                .errorMessage(ErrorCdMsg.FUNC_VERIFY_EMAIL_INVALID_CODE_ERR_MSG)
                                .build();
    }

    public static BaseErrorResponse createEmailExistsErrorResponse(final String email) {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.BAD_REQUEST.name())
                                .errorMessage(MessageFormat.format(ErrorCdMsg.FUNC_EMAIL_EXISTS_ERR_MSG,
                                                                   email))
                                .build();
    }

    public static BaseErrorResponse createEmailNotExistsErrorResponse(final String email) {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.BAD_REQUEST.name())
                                .errorMessage(MessageFormat.format(ErrorCdMsg.FUNC_EMAIL_NOT_EXISTS_ERR_MSG,
                                                                   email))
                                .build();
    }

    public static BaseErrorResponse createPermissionExistsErrorResponse(final String permissionNo) {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.BAD_REQUEST.name())
                                .errorMessage(MessageFormat.format(ErrorCdMsg.FUNC_PERMISSION_EXISTS_ERR_MSG,
                                                                   permissionNo))
                                .build();
    }

    public static BaseErrorResponse createBookCategoryNotFoundErrorResponse() {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.BAD_REQUEST.name())
                                .errorMessage(ErrorCdMsg.FUNC_BOOK_CATEGORY_NOT_FOUND_ERR_MSG)
                                .build();
    }

    public static BaseErrorResponse createBookCategorySlugExistsErrorResponse(final String slug) {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.BAD_REQUEST.name())
                                .errorMessage(MessageFormat.format(ErrorCdMsg.FUNC_BOOK_CATEGORY_SLUG_EXISTS_ERR_MSG,
                                                                   slug))
                                .build();
    }

    public static BaseErrorResponse createBookCategoryInvalidDepthErrorResponse(final int depth) {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.BAD_REQUEST.name())
                                .errorMessage(MessageFormat.format(ErrorCdMsg.FUNC_BOOK_CATEGORY_INVALID_DEPTH_ERR_MSG,
                                                                   depth))
                                .build();
    }

    public static BaseErrorResponse createBookPublisherNotFoundErrorResponse() {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.BAD_REQUEST.name())
                                .errorMessage(ErrorCdMsg.FUNC_BOOK_PUBLISHER_NOT_FOUND_ERR_MSG)
                                .build();
    }

    public static BaseErrorResponse createBookPublisherSlugExistsErrorResponse(final String slug) {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.BAD_REQUEST.name())
                                .errorMessage(MessageFormat.format(ErrorCdMsg.FUNC_BOOK_PUBLISHER_SLUG_EXISTS_ERR_MSG,
                                                                   slug))
                                .build();
    }

    public static BaseErrorResponse createAuthorNotFoundErrorResponse() {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.BAD_REQUEST.name())
                                .errorMessage(ErrorCdMsg.FUNC_BOOK_AUTHOR_NOT_FOUND_ERR_MSG)
                                .build();
    }

    public static BaseErrorResponse createAuthorSlugExistsErrorResponse(final String slug) {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.BAD_REQUEST.name())
                                .errorMessage(MessageFormat.format(ErrorCdMsg.FUNC_BOOK_AUTHOR_SLUG_EXISTS_ERR_MSG,
                                                                   slug))
                                .build();
    }

    public static BaseErrorResponse createBookNotFoundErrorResponse() {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.BAD_REQUEST.name())
                                .errorMessage(ErrorCdMsg.FUNC_BOOK_NOT_FOUND_ERR_MSG)
                                .build();
    }

    public static BaseErrorResponse createBookSlugExistsErrorResponse(final String slug) {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.BAD_REQUEST.name())
                                .errorMessage(MessageFormat.format(ErrorCdMsg.FUNC_BOOK_SLUG_EXISTS_ERR_MSG,
                                                                   slug))
                                .build();
    }

    public static BaseErrorResponse createRoleNoExistsErrorResponse(final String roleNo) {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.BAD_REQUEST.name())
                                .errorMessage(MessageFormat.format(ErrorCdMsg.FUNC_ROLE_NO_EXISTS_ERR_MSG,
                                                                   roleNo))
                                .build();
    }

    public static BaseErrorResponse createRoleNoNotExistsErrorResponse(final String roleNo) {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.BAD_REQUEST.name())
                                .errorMessage(MessageFormat.format(ErrorCdMsg.FUNC_ROLE_NO_NOT_EXISTS_ERR_MSG,
                                                                   roleNo))
                                .build();
    }

    public static BaseErrorResponse createRoleNotFoundErrorResponse() {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.BAD_REQUEST.name())
                                .errorMessage(ErrorCdMsg.FUNC_ROLE_NOT_FOUND_ERR_MSG)
                                .build();
    }

    public static BaseErrorResponse createRoleUpdatePermissionInvalidRequestErrorResponse() {
        return BaseErrorResponse.builder()
                                .errorCode(HttpStatus.BAD_REQUEST.name())
                                .errorMessage(ErrorCdMsg.FUNC_ROLE_UPDATE_PERMISSION_INVALID_REQUEST)
                                .build();
    }
}
