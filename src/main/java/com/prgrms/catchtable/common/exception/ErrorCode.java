package com.prgrms.catchtable.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    NOT_EXIST_MEMBER(HttpStatus.BAD_REQUEST, "존재하지 않는 아이디입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
