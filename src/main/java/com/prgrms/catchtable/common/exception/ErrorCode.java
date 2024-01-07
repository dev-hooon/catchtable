package com.prgrms.catchtable.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    NOT_EXIST_MEMBER("존재하지 않는 회원입니다."),
    NOT_FOUND_REFRESH_TOKEN("유효하지 않은 RefreshToken입니다."),
    TOKEN_EXPIRES("토큰이 만료되었습니다. 다시 로그인 해 주세요."),

    ALREADY_PREOCCUPIED_RESERVATION_TIME("이미 타인에게 선점권이 있는 예약시간입니다."),
    ALREADY_OCCUPIED_RESERVATION_TIME("이미 예약된 시간입니다."),
    NOT_EXIST_SHOP("존재하지 않는 매장입니다."),
    NOT_EXIST_TIME("존재하지 않는 예약 시간입니다."),
    NOT_EXIST_RESERVATION("존재하지 않는 예약입니다"),
    EXCEED_PEOPLE_COUNT("예약인원이 해당 시간의 남은 수용가능 인원 수를 초과했습니다."),
    ALREADY_COMPLETED_RESERVATION("이미 예약 상태인 예약입니다."),

    CAN_NOT_COMPLETE_WAITING("입장 처리가 불가한 대기 상태입니다."),
    EXISTING_MEMBER_WAITING("이미 회원이 웨이팅 중인 가게가 존재합니다."),
    ALREADY_END_LINE("이미 맨뒤라 웨이팅을 미룰 수 없습니다."),
    NOT_EXIST_PROGRESS_WAITING("진행 중인 웨이팅이 존재하지 않습니다."),

    POSTPONE_REMAINING_CNT_0("이미 두 차례 대기를 미뤘습니다."),
    CAN_NOT_CANCEL_WAITING("웨이팅 취소 처리가 불가한 상태입니다."),
    CAN_NOT_ENTRY("웨이팅을 입장 처리할 수 없습니다"),
    WAITING_DOES_NOT_EXIST("웨이팅이 존재하지 않습니다"),
    SHOP_NOT_RUNNING("가게가 영업시간이 아닙니다."),
    INTERNAL_SERVER_ERROR("내부 서버 오류입니다."),

    ALREADY_EXIST_OWNER("이미 존재하는 점주입니다"),
    BAD_REQUEST_EMAIL_OR_PASSWORD("이메일 혹은 비밀번호를 확인해주세요"),
    BAD_REQUEST_INPUT_GENDER_TYPE("성별 타입을 양식대로 입력해주세요");

    private final String message;
}