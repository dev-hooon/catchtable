package com.prgrms.catchtable.waiting.dto.response;

import lombok.Builder;

@Builder
public record MemberWaitingHistoryResponse(
    Long waitingId,
    Long shopId,
    String shopName,
    int peopleCount,
    String status
) {

}
