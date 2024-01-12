package com.prgrms.catchtable.shop.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ReservationTimeResponse(
    Long id,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")

    LocalDateTime reservationTime
) {

}
