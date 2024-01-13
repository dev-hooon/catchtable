package com.prgrms.catchtable.shop.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record GetAllShopResponse(
    Long id,
    String name,
    @JsonFormat(pattern = "HH:mm")
    LocalTime openingTime,
    @JsonFormat(pattern = "HH:mm")
    LocalTime closingTime
) {

}
