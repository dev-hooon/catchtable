package com.prgrms.catchtable.waiting.dto;

import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record CreateWaitingRequest(
    @Positive(message = "인원은 1명 이상이어야 합니다.")
    int peopleCount
) {

}
