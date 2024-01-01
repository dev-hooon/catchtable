package com.prgrms.catchtable.common.exception;

import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
        final MethodArgumentNotValidException exception) {
        StringBuilder sb = new StringBuilder();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            sb.append(fieldError.getDefaultMessage());
            sb.append(", ");
        }
        return ResponseEntity
            .badRequest()
            .body(new ErrorResponse(sb.toString()));
    }

    @ExceptionHandler(NotFoundCustomException.class)
    protected ResponseEntity<ErrorResponse> handleNotFoundException(
        final NotFoundCustomException exception) {
        return ResponseEntity
            .badRequest()
            .body(new ErrorResponse(exception.getErrorCode()));
    }

    @ExceptionHandler(BadRequestCustomException.class)
    protected ResponseEntity<ErrorResponse> handleBadRequestException(
        final BadRequestCustomException exception) {
        return ResponseEntity
            .badRequest()
            .body(new ErrorResponse(exception.getErrorCode()));
    }
}
