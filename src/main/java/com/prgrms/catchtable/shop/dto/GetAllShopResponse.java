package com.prgrms.catchtable.shop.dto;

import java.util.List;

public record GetAllShopResponse(
    List<GetShopResponse> shopResponses
) {

}
