package com.prgrms.catchtable.security.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.prgrms.catchtable.common.Role;
import com.prgrms.catchtable.common.base.BaseIntegrationTest;
import com.prgrms.catchtable.jwt.domain.RefreshToken;
import com.prgrms.catchtable.jwt.provider.JwtTokenProvider;
import com.prgrms.catchtable.jwt.repository.RefreshTokenRepository;
import com.prgrms.catchtable.jwt.service.RefreshTokenService;
import com.prgrms.catchtable.jwt.token.Token;
import com.prgrms.catchtable.member.MemberFixture;
import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.member.repository.MemberRepository;
import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.owner.fixture.OwnerFixture;
import com.prgrms.catchtable.owner.repository.OwnerRepository;
import com.prgrms.catchtable.reservation.dto.request.CreateReservationRequest;
import com.prgrms.catchtable.reservation.fixture.ReservationFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class JwtAuthenticationTest extends BaseIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private Member loginMember;
    private Owner loginOwner;
    private String memberEmail = "abc1234@gmail.com";
    private String ownerEmail = "qwer5678@naver.com";
    private Token memberToken;
    private Token ownerToken;
    private CreateReservationRequest createReservationRequest = ReservationFixture.getCreateReservationRequest();


    @BeforeEach
    public void init() {
        memberRepository.deleteAll();
        ownerRepository.deleteAll();
        refreshTokenRepository.deleteAll();

        //Member 객체 저장
        loginMember = MemberFixture.member(memberEmail);
        memberRepository.save(loginMember);

        //Owner 객체 저장
        loginOwner = OwnerFixture.getOwner();
        ownerRepository.save(loginOwner);

        //Member 토큰 발급
        memberToken = jwtTokenProvider.createToken(memberEmail, Role.MEMBER);
        refreshTokenRepository.save(RefreshToken.builder()
            .token(memberToken.getRefreshToken())
            .email(memberEmail)
            .role(Role.MEMBER)
            .build());

        //Owner 토큰 발급
        ownerToken = jwtTokenProvider.createToken(ownerEmail, Role.OWNER);
        refreshTokenRepository.save(RefreshToken.builder()
            .token(ownerToken.getRefreshToken())
            .email(ownerEmail)
            .role(Role.OWNER)
            .build());
    }

    @Test
    @DisplayName("Member의 AceessToken이 유효하다면, MemberWhiteList 접근이 가능하다.")
    void testMemberAccessToken() throws Exception {
        httpHeaders.add("AccessToken", memberToken.getAccessToken());
        httpHeaders.add("RefreshToken", memberToken.getRefreshToken());

        //Reservation 도메인
        mockMvc.perform(post("/reservations")
                .headers(httpHeaders)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createReservationRequest)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Member의 AccessToken이 유효하지 않아도 RefreshToken이 유효하다면, MemberWhiteList 접근이 가능하다.")
    void testMemberRefreshToken() throws Exception {
        httpHeaders.add("AccessToken", memberToken.getAccessToken() + "abc");
        httpHeaders.add("RefreshToken", memberToken.getRefreshToken());

        mockMvc.perform(post("/reservations")
                .headers(httpHeaders)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createReservationRequest)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Member의 AccessToken과 RefreshToken이 모두 유효하지 않다면, MemberWhiteList의 접근하지 못한다.")
    void testInvalidToken() throws Exception {
        httpHeaders.add("AccessToken", memberToken.getAccessToken() + "abc");
        httpHeaders.add("RefreshToken", memberToken.getRefreshToken() + "abc");

        mockMvc.perform(get("/reservations")
                .headers(httpHeaders)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createReservationRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Header의 토큰이 담겨있지 않은 상태로 WhiteList의 접근 시 401 에러를 반환한다. (인증 테스트)")
    void testNotContainsToken() throws Exception {
        mockMvc.perform(get("/reservations")
                .headers(httpHeaders)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createReservationRequest)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Member가 OwnerWhiteList의 접근 시 403 에러를 반환한다. (인가 테스트)")
    void testAuthorization() throws Exception {
        httpHeaders.add("AccessToken", memberToken.getAccessToken());
        httpHeaders.add("RefreshToken", memberToken.getRefreshToken());

        mockMvc.perform(get("/owners/shop")
                .headers(httpHeaders)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createReservationRequest)))
            .andExpect(status().isForbidden());
    }
}