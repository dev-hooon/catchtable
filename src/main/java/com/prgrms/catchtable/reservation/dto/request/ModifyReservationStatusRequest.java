package com.prgrms.catchtable.reservation.dto.request;

import com.prgrms.catchtable.reservation.domain.ReservationStatus;
import lombok.Builder;

@Builder
public record ModifyReservationStatusRequest(ReservationStatus status) {

}
