package com.prgrms.catchtable.reservation.dto.response;

import com.prgrms.catchtable.reservation.domain.ReservationStatus;
import lombok.Builder;

@Builder
public record CancelReservationResponse(ReservationStatus status) {

}
