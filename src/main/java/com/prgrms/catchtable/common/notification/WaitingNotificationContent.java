package com.prgrms.catchtable.common.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum WaitingNotificationContent {
    REGISTERED("웨이팅이 등록되었습니다.\n내 순서 : %d번째"),
    CANCELED("웨이팅이 취소되었습니다."),

    THIRD_RANK("3번째 순서로 곧 입장하실 차례입니다.");

    private final String message;
}