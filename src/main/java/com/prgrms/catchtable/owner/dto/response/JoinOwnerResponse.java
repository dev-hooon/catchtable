package com.prgrms.catchtable.owner.dto.response;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record JoinOwnerResponse(

    String name,
    String email,
    String phoneNumber,
    String gender,
    LocalDate dateBirth
) {

}
