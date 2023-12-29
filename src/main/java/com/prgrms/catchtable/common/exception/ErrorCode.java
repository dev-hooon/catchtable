package com.prgrms.catchtable.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    NOT_EXIST_MEMBER("존재하지 않는 아이디입니다."),
    ALREADY_PREOCCUPIED_RESERVATION_TIME("이미 타인에게 선점권이 있는 예약시간입니다."),
    ALREADY_OCCUPIED_RESERVATION_TIME("이미 예약된 시간입니다."),
    NOT_EXIST_SHOP("존재하지 않는 매장입니다."),
    NOT_EXIST_TIME("존재하지 않는 예약 시간입니다."),
    INTERNAL_SERVER_ERROR("내부 서버 오류입니다.");

    private final String message;
}
