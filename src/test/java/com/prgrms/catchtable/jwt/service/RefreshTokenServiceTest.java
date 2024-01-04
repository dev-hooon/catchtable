package com.prgrms.catchtable.jwt.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import com.prgrms.catchtable.jwt.config.JwtConfig;
import com.prgrms.catchtable.jwt.domain.RefreshToken;
import com.prgrms.catchtable.jwt.provider.JwtTokenProvider;
import com.prgrms.catchtable.jwt.repository.RefreshTokenRepository;
import com.prgrms.catchtable.jwt.token.Token;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    RefreshTokenRepository refreshTokenRepository;
    @InjectMocks
    RefreshTokenService refreshTokenService;

    JwtConfig jwtConfig = mock(JwtConfig.class);
    JwtUserDetailsService jwtUserDetailsService = mock(JwtUserDetailsService.class);
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(jwtConfig, jwtUserDetailsService);

    Token token;
    String email = "abc1234@gmail.com";
    String clientSecretKey = "FLGs0worldbOS8CEdfSPW04mb0dkD9SKFlsob9WK9wW0WkdlskYof5142u3jdmsk";

    @BeforeEach
    void init() {
        when(jwtConfig.getClientSecret()).thenReturn(clientSecretKey);
        when(jwtConfig.getExpiryMinute()).thenReturn(1);
        when(jwtConfig.getExpiryMinuteRefresh()).thenReturn(1);
        token = jwtTokenProvider.createToken(email);
    }

    @Test
    @DisplayName("새로운 유저의 RefreshToken이라면, DB에 바로 저장한다.")
    void saveRefreshTokenTest() {
        //when
        refreshTokenService.saveRefreshToken(token);

        //then
        verify(refreshTokenRepository, times(1)).existsRefreshTokenByEmail(email);
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));

        verify(refreshTokenRepository, never()).deleteRefreshTokenByEmail(email);
    }

    @Test
    @DisplayName("이미 유효한 RefreshToken을 갖고 있는 유저가 RefreshToken을 새로 발급한다면, DB에서 삭제 후 저장해준다.")
    void deleteAndSaveRefreshToken() {
        //given
        Token newToken = jwtTokenProvider.createToken(email);

        //when
        when(refreshTokenRepository.existsRefreshTokenByEmail(email)).thenReturn(true);
        refreshTokenService.saveRefreshToken(newToken);

        //then
        verify(refreshTokenRepository, times(1)).existsRefreshTokenByEmail(email);
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));

        verify(refreshTokenRepository, times(1)).deleteRefreshTokenByEmail(email);
    }

    @Test
    @DisplayName("refreshToken을 통해 RefreshToken Entity를 갖고온다.")
    void getRefreshTokenTest() {
        //given
        String invalidEmail = "qwer1234@naver.com";
        Token invalidToken = jwtTokenProvider.createToken(invalidEmail);

        RefreshToken refreshToken = RefreshToken.builder()
            .token(token.getRefreshToken())
            .email(token.getEmail())
            .build();

        //when
        when(refreshTokenRepository.findRefreshTokenByToken(token.getRefreshToken())).thenReturn(
            Optional.of(refreshToken));

        //then
        assertThat(refreshTokenService.getRefreshTokenByToken(token.getRefreshToken())).isEqualTo(
            refreshToken);
        assertThatThrownBy(() -> refreshTokenService.getRefreshTokenByToken(
            invalidToken.getRefreshToken())).isInstanceOf(
            NotFoundCustomException.class);
    }
}