package com.prgrms.catchtable.reservation.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {
    COMPLETED("예약 완료"),
    CANCELLED("예약 취소"),
    NO_SHOW("노쇼");

    private final String description;
}
