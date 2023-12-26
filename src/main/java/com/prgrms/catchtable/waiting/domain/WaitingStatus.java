package com.prgrms.catchtable.waiting.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum WaitingStatus {
    PROGRESS("웨이팅 진행 중"),
    COMPLETED("웨이팅 입장"),
    CANCELED("웨이팅 취소"),
    NO_SHOW("노쇼");

    private final String description;
}
