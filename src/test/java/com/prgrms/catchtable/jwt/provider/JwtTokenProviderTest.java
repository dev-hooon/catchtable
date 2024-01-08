package com.prgrms.catchtable.jwt.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.prgrms.catchtable.common.Role;
import com.prgrms.catchtable.jwt.config.JwtConfig;
import com.prgrms.catchtable.jwt.service.JwtUserDetailsService;
import com.prgrms.catchtable.jwt.token.Token;
import com.prgrms.catchtable.member.MemberFixture;
import com.prgrms.catchtable.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    private final String email = "abc1234@naver.com";
    private final String clientKey = "FLGs0worldbOS8CEdfSPW04mb0dkD9SKFlsob9WK9wW0WkdlskYof5142u3jdmsk";
    @Mock
    private JwtConfig config;
    @Mock
    private JwtUserDetailsService jwtUserDetailsService;
    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("유효한 토큰 발급 후 토큰 검증 시, true를 반환한다.")
    void validToken() {
        //when
        when(config.getClientSecret()).thenReturn(clientKey);
        when(config.getExpiryMinute()).thenReturn(1);
        when(config.getExpiryMinuteRefresh()).thenReturn(1);
        Token token = jwtTokenProvider.createToken(email, Role.MEMBER);

        //then
        assertThat(jwtTokenProvider.validateToken(token.getAccessToken())).isTrue();
        assertThat(jwtTokenProvider.validateToken(token.getRefreshToken())).isTrue();
    }

    @Test
    @DisplayName("유효 기간이 지난 토큰을 검증 시, false를 반환한다.")
    void invalidToken() {
        //when
        when(config.getClientSecret()).thenReturn(clientKey);
        when(config.getExpiryMinute()).thenReturn(0);
        when(config.getExpiryMinuteRefresh()).thenReturn(0);
        Token token = jwtTokenProvider.createToken(email, Role.OWNER);

        //then
        assertThat(jwtTokenProvider.validateToken(token.getAccessToken())).isFalse();
        assertThat(jwtTokenProvider.validateToken(token.getRefreshToken())).isFalse();
    }

    @Test
    @DisplayName("토큰을 통해서 Member Entity가 담긴 Authentication 반환")
    void getAuthenticationTest() {
        //given
        Member member = MemberFixture.member(email);

        //when
        when(config.getClientSecret()).thenReturn(clientKey);
        when(config.getExpiryMinute()).thenReturn(1);
        when(config.getExpiryMinuteRefresh()).thenReturn(1);
        Token token = jwtTokenProvider.createToken(email, Role.MEMBER);

        when(jwtUserDetailsService.loadUserByUsername(email, Role.MEMBER))
            .thenReturn(member);

        //then
        assertThat(jwtTokenProvider.getAuthentication(token.getAccessToken()).getPrincipal())
            .isEqualTo(member);
    }
}