package com.prgrms.catchtable.waiting.dto.response;

import lombok.Builder;

@Builder
public record OwnerWaitingResponse(
    Long waitingId,
    int waitingNumber,
    Long rank,
    int peopleCount,
    String status
) {

}
