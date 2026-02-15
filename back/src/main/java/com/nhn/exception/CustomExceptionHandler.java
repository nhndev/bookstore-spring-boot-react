package com.nhn.exception;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.nhn.model.dto.response.BaseErrorResponse;
import com.nhn.model.dto.response.ErrorMessage;
import com.nhn.util.ErrorMsgUtil;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseErrorResponse> onMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        final List<ErrorMessage> errors = new ArrayList<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            final FieldError fieldError = ((FieldError) error);

            errors.add(ErrorMessage.builder().field(fieldError.getField())
                                   .message(fieldError.getDefaultMessage())
                                   .build());
        });
        final BaseErrorResponse errorResponse = BaseErrorResponse.builder()
                                                                 .errorCode(HttpStatus.BAD_REQUEST.name())
                                                                 .errorMessages(errors)
                                                                 .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(errorResponse);
    }

    //	@ExceptionHandler({
    //			BadRequestException.class,
    //			BindException.class,
    //			MethodArgumentNotValidException.class,
    //			MethodArgumentTypeMismatchException.class,
    //			MissingServletRequestParameterException.class
    //	})
    //	@ResponseStatus(HttpStatus.BAD_REQUEST)
    //	public BaseErrorResponse handlerRequestException(final Exception ex) {
    //		return BaseErrorResponse.builder()
    //						.code(StatusMessage.BAD_REQUEST.label)
    //						.status(StatusMessage.BAD_REQUEST)
    //						.timestamp(new Date())
    //						.message(ex.getLocalizedMessage())
    //						.build();
    //
    //	}
    //
    //	@ExceptionHandler(NotFoundException.class)
    //	@ResponseStatus(HttpStatus.OK)
    //	public BaseResponse handleNotFoundException(final NotFoundException ex) {
    //		return BaseResponse.builder()
    //				.error(BaseErrorResponse.builder()
    //						.code(StatusMessage.NOT_FOUND.label)
    //						.status(StatusMessage.NOT_FOUND)
    //						.timestamp(new Date())
    //						.message(ex.getLocalizedMessage())
    //						.build())
    //				.build();
    //
    //	}
    //
    //    @ExceptionHandler({AuthenticationException.class})
    //    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    //    public ResponseEntity<BaseErrorResponse> unauthorizedException(final AuthenticationException ex) {
    //        final ResponseEntity<BaseErrorResponse> res = ResponseEntity.badRequest()
    //                                                                    .body(ErrorMsgUtil.createUnauthorizedErrorResponse(ex));
    //        log.warn("←Response (API AuthenticationException)：{}", res);
    //        return res;
    //    }
    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<BaseErrorResponse> onForbiddenException(final ForbiddenException e) {
        final ResponseEntity<BaseErrorResponse> res = ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                                    .body(e.getRes());
        log.warn("←Response (API ForbiddenException)：{}", res);
        return res;
    }

    @ExceptionHandler(FuncErrorException.class)
    public ResponseEntity<BaseErrorResponse> onFuncErrorException(final FuncErrorException e) {
        final ResponseEntity<BaseErrorResponse> res = ResponseEntity.badRequest()
                                                                    .body(e.getRes());
        log.warn("←Response (API FuncErrorException)：{}", res);
        return res;
    }

    @ExceptionHandler(FileErrorException.class)
    public ResponseEntity<BaseErrorResponse> onFileErrorException(final FileErrorException e) {
        final ResponseEntity<BaseErrorResponse> res = ResponseEntity.badRequest()
                                                                    .body(e.getRes());
        log.warn("←Response (API FileErrorException)：{}", res);
        return res;
    }

    @ExceptionHandler(CloudinaryErrorException.class)
    public ResponseEntity<BaseErrorResponse> onCloudinaryErrorException(final CloudinaryErrorException e) {
        final ResponseEntity<BaseErrorResponse> res = ResponseEntity.badRequest()
                                                                    .body(e.getRes());
        log.warn("←Response (API CloudinaryErrorException)：{}", res);
        return res;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<BaseErrorResponse> onMaxUploadSizeExceededException(final MaxUploadSizeExceededException e) {
        final ResponseEntity<BaseErrorResponse> res = ResponseEntity.badRequest()
                                                                    .body(ErrorMsgUtil.createMaxUploadSizeExceededExceptionErrorResponse());
        log.warn("←Response (API CloudinaryErrorException)：{}", res);
        return res;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<BaseErrorResponse> handleAllException(final Exception ex,
                                                                final WebRequest webRequest) {
        log.error("handleAllException: ", ex);
        final ResponseEntity<BaseErrorResponse> res = ResponseEntity.internalServerError()
                                                                    .body(ErrorMsgUtil.createInternalServerErrorResponse());
        log.warn("←Response (API InternalServerError)：{}", res);
        return res;
    }
}
