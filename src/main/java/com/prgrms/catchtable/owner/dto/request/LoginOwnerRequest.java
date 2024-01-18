package com.prgrms.catchtable.owner.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Builder;

@Builder
public record LoginOwnerRequest(

    @Email
    String email,
    String password

) {

}
