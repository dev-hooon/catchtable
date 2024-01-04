package com.prgrms.catchtable.owner.dto.request;

import jakarta.validation.constraints.Email;

public record LoginOwnerRequest(

    @Email
    String email,
    String password

) {

}
