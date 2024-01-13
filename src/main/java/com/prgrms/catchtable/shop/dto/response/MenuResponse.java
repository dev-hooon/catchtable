package com.prgrms.catchtable.shop.dto.response;

import lombok.Builder;

@Builder
public record MenuResponse(
    Long id,
    String name,
    int price,
    String description
) {

}
