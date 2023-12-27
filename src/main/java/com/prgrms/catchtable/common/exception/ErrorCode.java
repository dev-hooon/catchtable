package com.prgrms.catchtable.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    NOT_EXIST_MEMBER("존재하지 않는 아이디입니다."),
    IS_PRE_OCCUPIED("이미 타인에게 선점권이 있는 예약시간입니다."),
    IS_OCCUPIED("이미 예약된 시간입니다."),
    NOT_EXIST_SHOP("존재하지 않는 매장입니다."),
    NOT_EXIST_TIME("존재하지 않는 예약 시간입니다.");

    private final String message;
}
