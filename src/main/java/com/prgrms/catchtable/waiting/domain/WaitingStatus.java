package com.prgrms.catchtable.waiting.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WaitingStatus {
    PROGRESS("진행 중"),
    COMPLETED("입장 완료"),
    CANCELED("취소"),
    NO_SHOW("노쇼");

    private final String description;

}
