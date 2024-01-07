package com.prgrms.catchtable.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationContent {
    RESERVATION_COMPLETED("예약이 완료되었습니다"),
    RESERVATION_ONE_HOUR_LEFT("예약 시간 1시간 전입니다."),
    RESERVATION_TIME_TO_ENTER("예약시간이 되었습니다"),
    WAITING_REGISTER_COMPLETED("웨이팅 등록이 완료되었습니다"),
    WAITING_RANK_THIRD("웨이팅 순서가 3번째가 되었습니다"),
    WAITING_TIME_TO_ENTER("웨이팅이 끝났습니다. 입장 부탁드립니다."),
    WAITING_CANCELLED_AUTOMATICALLY("웨이팅이 자동으로 취소되었습니다.");

    private final String message;


}
