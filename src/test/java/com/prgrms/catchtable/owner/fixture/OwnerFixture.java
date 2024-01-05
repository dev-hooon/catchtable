package com.prgrms.catchtable.owner.fixture;

import com.prgrms.catchtable.member.domain.Gender;
import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.owner.dto.request.JoinOwnerRequest;
import com.prgrms.catchtable.owner.dto.request.LoginOwnerRequest;
import java.time.LocalDate;

public class OwnerFixture {

    public static Owner getOwner(String email, String encodePassword){
        return Owner.builder()
            .name("test")
            .email(email)
            .password(encodePassword)
            .phoneNumber("010-8830-4795")
            .gender(Gender.MALE)
            .dateBirth(LocalDate.of(1998,3,25))
            .build();
    }

    public static JoinOwnerRequest getJoinOwnerRequest(String email, String password){
        return JoinOwnerRequest.builder()
            .name("test")
            .email(email)
            .password(password)
            .phoneNumber("010-8830-4795")
            .gender("male")
            .dateBirth(LocalDate.of(1998,3,25))
            .build();
    }

    public static LoginOwnerRequest getLoginOwnerRequest(String email, String password){
        return LoginOwnerRequest.builder()
            .email(email)
            .password(password)
            .build();
    }
}
