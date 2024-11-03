package com.github.travel.common.handler;

import com.github.commonlib.ApiException;
import com.github.commonlib.ErrorCode;
import com.github.commonlib.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(Exception e) {
        log.error("Exception: {}" , e);

        ErrorResponse response = new ErrorResponse(ErrorCode.BAD_REQUEST, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException e) {
        log.error("Exception: {}" , e);

        ErrorResponse response = new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationExceiption(AuthenticationException e){
        log.error("Exception: {}" , e);

        ErrorResponse response = new ErrorResponse(ErrorCode.LOGIN_FAIL, "계정 정보가 맞지 않습니다.");
        return ResponseEntity.status(ErrorCode.AUTHENTICAITON_ERROR.getHttpStatus()).body(response);
    }

}
