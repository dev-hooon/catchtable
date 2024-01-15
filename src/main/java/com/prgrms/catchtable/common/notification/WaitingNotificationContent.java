package com.prgrms.catchtable.common.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum WaitingNotificationContent {
    MEMBER_CREATED("웨이팅이 등록되었습니다.\n내 순서 : %d번째"),
    MEMBER_CANCELED("웨이팅이 취소되었습니다."),
    MEMBER_COMPLETED("가게에 입장 처리되었습니다."),
    MEMBER_ENTRY("웨이팅 입장할 차례입니다."),
    MEMBER_POSTPONED("웨이팅 순서를 %d번으로 미뤘습니다."),
    THIRD_RANK("3번째 순서로 곧 입장하실 차례입니다."),
    FIRST_RANK("입장할 순서예요! 지금 매장으로 와주세요."),
    OWNER_CREATED("%d번째 웨이팅이 등록되었습니다."),
    OWNER_CANCELED("%d번째 웨이팅이 취소되었습니다.");

    private final String content;
}
