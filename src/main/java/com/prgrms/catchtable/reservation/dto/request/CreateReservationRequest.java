package com.prgrms.catchtable.reservation.dto.request;

import lombok.Builder;

@Builder
public record CreateReservationRequest(Long reservationTimeId,
                                       int peopleCount) {

}
