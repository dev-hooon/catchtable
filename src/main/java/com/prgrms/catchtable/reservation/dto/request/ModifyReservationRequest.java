package com.prgrms.catchtable.reservation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ModifyReservationRequest(Long reservationTimeId,
                                       int peopleCount) {

}
