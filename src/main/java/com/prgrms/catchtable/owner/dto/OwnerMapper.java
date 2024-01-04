package com.prgrms.catchtable.owner.dto;

import com.prgrms.catchtable.member.domain.Gender;
import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.owner.dto.request.JoinOwnerRequest;
import com.prgrms.catchtable.owner.dto.response.JoinOwnerResponse;

public class OwnerMapper {

    public static Owner toEntity(JoinOwnerRequest joinOwnerRequest, String encodePassword, Gender gender){
        return Owner.builder()
            .name(joinOwnerRequest.name())
            .email(joinOwnerRequest.email())
            .password(encodePassword)
            .phoneNumber(joinOwnerRequest.phoneNumber())
            .gender(gender)
            .dateBirth(joinOwnerRequest.dateBirth())
            .build();
    }

    public static JoinOwnerResponse from(Owner owner){
        return JoinOwnerResponse.builder()
            .name(owner.getName())
            .email(owner.getEmail())
            .phoneNumber(owner.getPhoneNumber())
            .gender(owner.getGender().getType())
            .dateBirth(owner.getDateBirth())
            .build();
    }

}
