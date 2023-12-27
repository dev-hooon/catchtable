package com.prgrms.catchtable.reservation.dto.response;

import com.prgrms.catchtable.reservation.domain.ReservationTime;
import lombok.Builder;

@Builder
public record ValidateReservationResponse(String shopName,
                                          ReservationTime reservationTime) {

}
