package com.prgrms.catchtable.member.service;

import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.member.dto.MemberMapper;
import com.prgrms.catchtable.member.repository.MemberRepository;
import com.prgrms.catchtable.security.dto.OAuthAttribute;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void oauthLogin(OAuthAttribute attributes) {
        String email = attributes.getEmail();
        Optional<Member> optionalMember = memberRepository.findMemberByEmail(email);

        if (optionalMember.isEmpty()) {
            Member entity = MemberMapper.changeOAuth(attributes);
            memberRepository.save(entity);
        }

        //JWT 토큰 생성 & 반환
    }

}
