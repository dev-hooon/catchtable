package com.prgrms.catchtable.reservation.dto.response;

import com.prgrms.catchtable.reservation.domain.ReservationTime;

public record ValidateReservationResponse(String shopName, ReservationTime reservationTime) {

}
