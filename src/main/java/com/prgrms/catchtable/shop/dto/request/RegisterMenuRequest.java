package com.prgrms.catchtable.shop.dto.request;

public record RegisterMenuRequest(
    String name,
    int price,
    String description
) {

}
