package com.prgrms.catchtable.waiting.dto.response;

import lombok.Builder;

@Builder
public record MemberWaitingResponse(
    Long waitingId,
    Long shopId,
    String shopName,
    int peopleCount,
    int waitingNumber,
    Long rank,
    int remainingPostponeCount,
    String status
) {

}