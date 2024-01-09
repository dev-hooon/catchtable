package com.prgrms.catchtable.shop.dto.response;

import java.util.List;

public record GetAllShopResponse(
    List<GetShopResponse> shopResponses
) {

}
