package com.prgrms.catchtable.waiting.dto;

import lombok.Builder;

@Builder
public record WaitingResponse(
    Long createdWaitingId,
    Long shopId,
    String shopName,
    int peopleCount,
    int waitingNumber,
    Long rank
) {

}