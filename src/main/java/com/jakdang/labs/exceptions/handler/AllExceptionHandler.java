package com.jakdang.labs.exceptions.handler;

import com.jakdang.labs.exceptions.ErrorResponse;
import com.jakdang.labs.exceptions.ErrorType;
import com.jakdang.labs.exceptions.ResultMessageEnum;
import com.jakdang.labs.exceptions.ValidationError;
import com.jakdang.labs.api.common.ResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class AllExceptionHandler {

    @ExceptionHandler({AuthorizationDeniedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ResponseDTO<?>> handleAuthorizationException(AuthorizationDeniedException e, HttpServletRequest request){

        log.error("AuthorizationDeniedException 발생 [권한 에러 ROLE값 확인 요청] - Error Code: 401, URI: {}", request.getRequestURI());
        return ResponseEntity
                .status(-401)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ResponseDTO.createErrorResponse(401, e.getMessage()));
    }

    @ExceptionHandler({FileException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse<ErrorType>> handleFileException(FileException e, HttpServletRequest request) {
        ErrorResponse<ErrorType> response = ErrorResponse.of(
                e.getErrorCode(),
                request.getRequestURI()
        );
        log.error("FileException 발생 - Error Code: {}, URI: {}", e.getErrorCode(), request.getRequestURI());
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @ExceptionHandler({JwtException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse<ErrorType>> handleJwtException(JwtException e, HttpServletRequest request) {
        ErrorResponse<ErrorType> response = ErrorResponse.of(
                e.getErrorCode(),
                request.getRequestURI()
        );
        log.error("JwtException 발생 - Error Code: {}, URI: {}", e.getErrorCode(), request.getRequestURI());
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<ErrorResponse<ErrorType>> handleValidationException(MethodArgumentNotValidException e) {
        List<ValidationError> validationErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> ValidationError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .rejectedValue(error.getRejectedValue())
                        .build())
                .toList();

        ErrorResponse<ErrorType> response = ErrorResponse.builder()
                .statusCode(400)
                .message("입력값이 올바르지 않습니다.")
                .errors(validationErrors)
                .build();

        return ResponseEntity
                .badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @ExceptionHandler({Exception.class})
    protected ResponseEntity<ResponseDTO<?>> handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("알 수 없는 에러 발생 에러 메세지 : ======> {} {}", requestURI, e.getMessage() );
        ErrorResponse<ErrorType> response = ErrorResponse.builder()
                .statusCode(ResultMessageEnum.STATUS_ERROR.getStatus())
                .message(ResultMessageEnum.STATUS_ERROR.getMessage())
                .build();
        log.info(response.toString());
        return ResponseEntity
                .status(response.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ResponseDTO.createErrorResponse(500, e.getMessage()));
    }
}