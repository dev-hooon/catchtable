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
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex) {
        StringBuilder sb = new StringBuilder();
        for (FieldError e : ex.getBindingResult().getFieldErrors()) {
            sb.append(e.getDefaultMessage());
            sb.append(", ");
        }
        return ResponseEntity
            .badRequest()
            .body(new ErrorResponse(sb.toString()));
    }

    @ExceptionHandler(NotFoundCustomException.class)
    protected ResponseEntity<ErrorResponse> handleNotFoundException(final NotFoundCustomException ex) {
        return ResponseEntity
            .badRequest()
            .body(new ErrorResponse(ex.getErrorCode()));
    }

    @ExceptionHandler(BadRequestCustomException.class)
    protected ResponseEntity<ErrorResponse> handleBadRequestException(final BadRequestCustomException ex){
        return ResponseEntity
            .badRequest()
            .body(new ErrorResponse(ex.getErrorCode()));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleServerException(final Exception ex) {
        return ResponseEntity
            .internalServerError()
            .body(new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
