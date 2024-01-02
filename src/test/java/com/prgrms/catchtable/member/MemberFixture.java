package com.prgrms.catchtable.member;

import com.prgrms.catchtable.member.domain.Gender;
import com.prgrms.catchtable.member.domain.Member;
import java.time.LocalDate;

public class MemberFixture {

    public static Member member(String name) {
        return Member.builder()
            .name(name)
            .phoneNumber("010-1111-1111")
            .gender(Gender.FEMALE)
            .dateBirth(LocalDate.parse("2008-12-18"))
            .build();
    }
}
