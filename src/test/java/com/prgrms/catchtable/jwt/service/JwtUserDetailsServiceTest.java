package com.prgrms.catchtable.jwt.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import com.prgrms.catchtable.member.MemberFixture;
import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtUserDetailsServiceTest {

    @Mock
    MemberRepository memberRepository;
    @InjectMocks
    JwtUserDetailsService jwtUserDetailsService;
    String email = "abc1234@gmail.com";
    String invalidEmail = "qwer@7890@naver.com";

    @Test
    @DisplayName("email을 통해 Member entity를 갖고온다")
    void loadUserByUsernameTest() {
        //given
        Member member = MemberFixture.userDetailsMember(email);

        //when
        when(memberRepository.findMemberByEmail(email)).thenReturn(Optional.of(member));
        when(memberRepository.findMemberByEmail(invalidEmail)).thenReturn(Optional.empty());

        //then
        assertThat(jwtUserDetailsService.loadUserByUsername(email)).isEqualTo(member);
        assertThatThrownBy(
            () -> jwtUserDetailsService.loadUserByUsername(invalidEmail)).isInstanceOf(
            NotFoundCustomException.class);

    }
}