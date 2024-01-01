package com.prgrms.catchtable.member.dto;

import com.prgrms.catchtable.member.domain.Gender;
import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.security.dto.OAuthAttribute;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MemberMapper {

    public static Member changeOAuth(OAuthAttribute attributes) {

        String birthString = attributes.getBirthYear() + attributes.getBirthDay();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate birthDay = LocalDate.parse(birthString, dateTimeFormatter);
        System.out.println(birthDay);

        return Member.builder()
            .name(attributes.getName())
            .email(attributes.getEmail())
            .phoneNumber(attributes.getPhoneNumber())
            .gender(attributes.getGender().equals("male") ? Gender.MALE : Gender.FEMALE)
            .dateBirth(birthDay)
            .build();
    }

}
