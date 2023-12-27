package com.prgrms.catchtable.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    NOT_EXIST_MEMBER("존재하지 않는 아이디입니다."),
    NOT_EXIST_SHOP("존재하지 않는 가게 아이디입니다."),

    ALREADY_CANCELED_WAITING("이미 웨이팅을 취소하였습니다."),
    EXISTING_MEMBER_WAITING("이미 회원이 웨이팅 중인 가게가 존재합니다."),
    SHOP_NOT_RUNNING("가게가 영업시간이 아닙니다."),
    INTERNAL_SERVER_ERROR("내부 서버 오류입니다.");
    private final String message;
}
