package com.prgrms.catchtable.shop.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;

@Builder
public record RegisterShopResponse(

    Long id,
    String name,
    BigDecimal rating,
    String category,
    String city,
    String district,
    int capacity,
    @JsonFormat(pattern = "kk:mm:ss")
    LocalTime openingTime,
    @JsonFormat(pattern = "kk:mm:ss")
    LocalTime closingTime,
    List<ReservationTimeResponse> reservationTimeResponseList,
    List<MenuResponse> menuResponseList
) {

}
