package com.prgrms.catchtable.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    NOT_EXIST_MEMBER("존재하지 않는 회원입니다."),
    NOT_FOUND_REFRESH_TOKEN("알 수 없는 RefreshToken입니다."),
    TOKEN_EXPIRES("토큰이 만료되었습니다. 다시 로그인 해 주세요."),

    ALREADY_PREOCCUPIED_RESERVATION_TIME("이미 타인에게 선점권이 있는 예약시간입니다."),
    ALREADY_OCCUPIED_RESERVATION_TIME("이미 예약된 시간입니다."),
    NOT_EXIST_SHOP("존재하지 않는 매장입니다."),
    NOT_EXIST_TIME("존재하지 않는 예약 시간입니다."),

    ALREADY_CANCELED_WAITING("이미 웨이팅을 취소하였습니다."),
    EXISTING_MEMBER_WAITING("이미 회원이 웨이팅 중인 가게가 존재합니다."),
    SHOP_NOT_RUNNING("가게가 영업시간이 아닙니다."),
    INTERNAL_SERVER_ERROR("내부 서버 오류입니다.");

    private final String message;
}
