package com.prgrms.catchtable.reservation.dto.request;

import java.time.LocalDateTime;

public record CreateResercationRequest(LocalDateTime date, int peopleCount) {

}
