package com.prgrms.catchtable.reservation.dto.request;

import java.time.LocalDateTime;

public record CreateReservationRequest(LocalDateTime date, int peopleCount) {

}
