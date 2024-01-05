package com.prgrms.catchtable.owner.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record JoinOwnerRequest(

    String name,
    @Email
    String email,
    String password,
    @Pattern(regexp = "^(01[016789]){1}([0-9]{3,4}){1}([0-9]{4}){1}$")
    String phoneNumber,
    String gender,
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dateBirth
) {

}
