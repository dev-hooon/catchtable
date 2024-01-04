package com.prgrms.catchtable.reservation.dto.request;

import lombok.Builder;

@Builder
public record ModifyReservationRequest(Long reservationTimeId,
                                       int peopleCount) {

}
