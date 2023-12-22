package com.prgrms.catchtable.reservation.dto.response;

import java.time.LocalDateTime;

public record CreateReservationResponse(String shopName, String memberName, LocalDateTime date, int peopleCount) {

}
