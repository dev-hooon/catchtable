package com.prgrms.catchtable.shop.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;

@Builder
public record RegisterShopRequest(

    String name,
    @Min(0)
    @Max(5)
    BigDecimal rating,
    String category,
    String city,
    String district,
    int capacity,
    @JsonFormat(pattern = "HH:mm")
    LocalTime openingTime,
    @JsonFormat(pattern = "HH:mm")
    LocalTime closingTime,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    List<LocalDateTime> reservationTimeRequestList,
    List<RegisterMenuRequest> menuRequestList
) {

}
