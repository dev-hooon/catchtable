package com.prgrms.catchtable.waiting.dto;

import lombok.Builder;

@Builder
public record WaitingResponse(
    Long waitingId,
    Long shopId,
    String shopName,
    int peopleCount,
    int waitingNumber,
    Long rank,
    int remainingPostponeCount
) {

}