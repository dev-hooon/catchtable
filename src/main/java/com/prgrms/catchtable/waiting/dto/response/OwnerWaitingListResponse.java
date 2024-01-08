package com.prgrms.catchtable.waiting.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record OwnerWaitingListResponse (
    List<OwnerWaitingResponse> shopWaitings
){}
