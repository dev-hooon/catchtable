package com.prgrms.catchtable.security.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.prgrms.catchtable.jwt.filter.JwtAuthenticationFilter;
import com.prgrms.catchtable.jwt.provider.JwtTokenProvider;
import com.prgrms.catchtable.jwt.service.RefreshTokenService;
import com.prgrms.catchtable.jwt.token.Token;
import com.prgrms.catchtable.member.MemberFixture;
import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.member.repository.MemberRepository;
import com.prgrms.catchtable.security.filter.ExceptionHandlerFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@WebAppConfiguration
class JwtAuthenticationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ExceptionHandlerFilter exceptionHandlerFilter;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    private MockMvc mockMvc;

    private Member loginMember;
    private String email = "abc1234@gmail.com";
    private Token token;

    @BeforeEach
    public void init() {
        //Member 객체 저장
        loginMember = MemberFixture.member(email);
        memberRepository.save(loginMember);

        //토큰 발급
        token = jwtTokenProvider.createToken(email);
        refreshTokenService.saveRefreshToken(token);

        //필터 추가
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .addFilters(exceptionHandlerFilter, jwtAuthenticationFilter)
            .build();
    }

    @Test
    @DisplayName("Member의 AceessToken이 유효하다면, Member 권한을 갖는다.")
    void testMemberAccessToken() throws Exception {
        mockMvc.perform(get("/testMember")
                .header("AccessToken", token.getAccessToken())
                .header("RefreshToken", token.getRefreshToken()))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Member의 AccessToken이 유효하지 않고, RefreshToken이 유효하다면, Member 권한을 갖는다.")
    void testMemberRefreshToken() throws Exception {
        mockMvc.perform(get("/testMember")
                .header("AccessToken", token.getAccessToken() + "abc")
                .header("RefreshToken", token.getRefreshToken()))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Member의 AccessToken과 RefreshToken이 모두 유효하지 않다면, 400 Error를 반환한다.")
    void testInvalidToken() throws Exception {
        mockMvc.perform(get("/testMember")
                .header("AccessToken", token.getAccessToken() + "abc")
                .header("RefreshToken", token.getRefreshToken() + "abc"))
            .andExpect(status().isBadRequest());
    }
}