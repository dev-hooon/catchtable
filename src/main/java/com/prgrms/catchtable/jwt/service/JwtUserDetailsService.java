package com.prgrms.catchtable.jwt.service;

import static com.prgrms.catchtable.common.exception.ErrorCode.NOT_EXIST_MEMBER;
import static com.prgrms.catchtable.common.exception.ErrorCode.NOT_EXIST_OWNER;

import com.prgrms.catchtable.common.Role;
import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import com.prgrms.catchtable.member.repository.MemberRepository;
import com.prgrms.catchtable.owner.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService{

    private final MemberRepository memberRepository;
    private final OwnerRepository ownerRepository;

    public UserDetails loadUserByUsername(String email, Role role) throws UsernameNotFoundException {

        if(role.equals(Role.MEMBER)){
            return memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new NotFoundCustomException(NOT_EXIST_MEMBER));
        }

        else{
            return ownerRepository.findOwnerByEmail(email)
                .orElseThrow(() -> new NotFoundCustomException(NOT_EXIST_OWNER));
        }
    }
}
