package com.prgrms.catchtable.reservation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.prgrms.catchtable.reservation.domain.ReservationStatus;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record OwnerGetAllReservationResponse(Long reservationId,
                                             @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
                                             LocalDateTime date,
                                             int peopleCount,
                                             ReservationStatus status) {

}
