package com.prgrms.catchtable.shop.dto;

import java.math.BigDecimal;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record GetShopResponse(

    Long id,
    String name,
    BigDecimal rating,
    String category,
    String city,
    String district,
    int capacity,
    LocalTime openingTime,
    LocalTime closingTime

) {

}
