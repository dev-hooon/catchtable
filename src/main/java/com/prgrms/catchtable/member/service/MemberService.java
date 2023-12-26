package com.prgrms.catchtable.member.service;

import com.prgrms.catchtable.jwt.provider.JwtTokenProvider;
import com.prgrms.catchtable.jwt.service.RefreshTokenService;
import com.prgrms.catchtable.jwt.token.Token;
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

    private final JwtTokenProvider jwtTokenProvider;

    private final RefreshTokenService refreshTokenService;

    @Transactional
    public Token oauthLogin(OAuthAttribute attributes) {
        String email = attributes.getEmail();
        Optional<Member> optionalMember = memberRepository.findMemberByEmail(email);

        if (optionalMember.isEmpty()) {
            Member entity = MemberMapper.changeOAuth(attributes);
            memberRepository.save(entity);
        }

        return createTotalToken(email);
    }

    private Token createTotalToken(String email){
        Token totalToken = jwtTokenProvider.createToken(email);
        refreshTokenService.saveRefreshToken(totalToken);
        return totalToken;
    }
}
