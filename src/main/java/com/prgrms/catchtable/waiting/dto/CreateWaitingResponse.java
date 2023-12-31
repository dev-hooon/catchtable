package com.prgrms.catchtable.waiting.dto;

import lombok.Builder;

@Builder
public record CreateWaitingResponse(
    Long createdWaitingId,
    Long shopId,
    String shopName,
    int peopleCount,
    int waitingNumber,
    int waitingOrder
){}