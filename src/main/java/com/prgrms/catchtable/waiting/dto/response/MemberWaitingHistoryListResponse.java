package com.prgrms.catchtable.waiting.dto.response;

import java.util.List;

public record MemberWaitingHistoryListResponse(
    List<MemberWaitingHistoryResponse> memberWaitings
) {

}
