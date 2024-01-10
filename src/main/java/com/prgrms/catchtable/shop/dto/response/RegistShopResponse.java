package com.prgrms.catchtable.shop.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record RegistShopResponse(

    String name,
    BigDecimal rating,
    String category,
    String city,
    String district,
    int capacity,
    @JsonFormat(pattern = "kk:mm:ss")
    LocalTime openingTime,
    @JsonFormat(pattern = "kk:mm:ss")
    LocalTime closingTime
) {

}
