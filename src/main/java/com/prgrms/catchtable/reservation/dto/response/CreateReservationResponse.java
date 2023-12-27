package com.prgrms.catchtable.reservation.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CreateReservationResponse(String shopName,
                                        String memberName,
                                        LocalDateTime date,
                                        int peopleCount) {

}
