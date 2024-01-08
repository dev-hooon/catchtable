package com.prgrms.catchtable.owner.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.jwt.provider.JwtTokenProvider;
import com.prgrms.catchtable.jwt.token.Token;
import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.owner.dto.request.JoinOwnerRequest;
import com.prgrms.catchtable.owner.dto.request.LoginOwnerRequest;
import com.prgrms.catchtable.owner.dto.response.JoinOwnerResponse;
import com.prgrms.catchtable.owner.fixture.OwnerFixture;
import com.prgrms.catchtable.owner.repository.OwnerRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class OwnerServiceTest {

    private final OwnerRepository ownerRepository = mock(OwnerRepository.class);
    private final JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final OwnerService ownerService = new OwnerService(ownerRepository, passwordEncoder,
        jwtTokenProvider);

    private final String email = "abc1234@gmail.com";
    private final String password = "qwer1234";
    private final String wrongPassword = "qwer12345";


    @Test
    @DisplayName("유저의 회원가입이 성공한다.")
    void joinSuccess() {
        //given
        JoinOwnerRequest joinOwnerRequest = OwnerFixture.getJoinOwnerRequest(email, password);
        String encodePassword = passwordEncoder.encode(password);

        //when
        when(ownerRepository.existsOwnerByEmail(joinOwnerRequest.email())).thenReturn(false);
        when(ownerRepository.save(any(Owner.class))).thenReturn(
            OwnerFixture.getOwner(email, encodePassword));
        JoinOwnerResponse joinOwnerResponse = ownerService.joinOwner(joinOwnerRequest);

        //then
        assertThat(joinOwnerRequest.name()).isEqualTo(joinOwnerResponse.name());
        assertThat(joinOwnerRequest.email()).isEqualTo(joinOwnerResponse.email());
        assertThat(joinOwnerRequest.gender()).isEqualTo(joinOwnerResponse.gender());
        assertThat(joinOwnerRequest.phoneNumber()).isEqualTo(joinOwnerResponse.phoneNumber());
        assertThat(joinOwnerRequest.dateBirth()).isEqualTo(joinOwnerResponse.dateBirth());
    }

    @Test
    @DisplayName("이미 회원가입한 이메일로 회원가입 시도 시, 예외 발생한다.")
    void joinFailure() {
        //given
        JoinOwnerRequest joinOwnerRequest = OwnerFixture.getJoinOwnerRequest(email, password);

        //when
        when(ownerRepository.existsOwnerByEmail(joinOwnerRequest.email())).thenReturn(true);

        //then
        assertThatThrownBy(() -> ownerService.joinOwner(joinOwnerRequest)).isInstanceOf(
            BadRequestCustomException.class);
    }

    @Test
    @DisplayName("로그인을 성공하면, 토큰을 반환한다")
    void loginSuccess() {
        //given
        LoginOwnerRequest loginOwnerRequest = OwnerFixture.getLoginOwnerRequest(email, password);
        String encodePassword = passwordEncoder.encode(password);
        Token token = new Token("AccessToken", "RefreshToken", loginOwnerRequest.email());

        //when
        when(ownerRepository.findOwnerByEmail(loginOwnerRequest.email())).thenReturn(
            Optional.of(OwnerFixture.getOwner(email, encodePassword)));
        when(jwtTokenProvider.createToken(loginOwnerRequest.email())).thenReturn(token);

        //then
        assertThat(ownerService.loginOwner(loginOwnerRequest)).isEqualTo(token);
    }

    @Test
    @DisplayName("해당 이메일의 유저가 존재하지 않으면 로그인을 실패한다")
    void loginFailureId() {
        //given
        LoginOwnerRequest loginOwnerRequest = OwnerFixture.getLoginOwnerRequest(email, password);

        //when
        when(ownerRepository.findOwnerByEmail(loginOwnerRequest.email())).thenReturn(
            Optional.empty());

        //then
        assertThatThrownBy(() -> ownerService.loginOwner(loginOwnerRequest)).isInstanceOf(
            BadRequestCustomException.class);
    }

    @Test
    @DisplayName("비밀번호가 다르면 로그인을 실패한다.")
    void loginFailurePassword() {
        //given
        LoginOwnerRequest loginOwnerRequest = OwnerFixture.getLoginOwnerRequest(email,
            wrongPassword);
        String encodePassword = passwordEncoder.encode(password);

        //when
        when(ownerRepository.findOwnerByEmail(loginOwnerRequest.email())).thenReturn(
            Optional.of(OwnerFixture.getOwner(email, encodePassword)));

        //then
        assertThatThrownBy(() -> ownerService.loginOwner(loginOwnerRequest)).isInstanceOf(
            BadRequestCustomException.class);
    }
}