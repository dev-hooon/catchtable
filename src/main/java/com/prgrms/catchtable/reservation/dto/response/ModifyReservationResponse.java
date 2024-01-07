package com.prgrms.catchtable.reservation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ModifyReservationResponse(String shopName,
                                        String memberName,
                                        @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
                                        LocalDateTime date,
                                        int peopleCount) {

}
