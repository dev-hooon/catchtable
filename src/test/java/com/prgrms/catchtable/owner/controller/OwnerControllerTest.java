package com.prgrms.catchtable.owner.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.prgrms.catchtable.common.Role;
import com.prgrms.catchtable.common.base.BaseIntegrationTest;
import com.prgrms.catchtable.jwt.provider.JwtTokenProvider;
import com.prgrms.catchtable.jwt.token.Token;
import com.prgrms.catchtable.owner.dto.request.JoinOwnerRequest;
import com.prgrms.catchtable.owner.dto.request.LoginOwnerRequest;
import com.prgrms.catchtable.owner.fixture.OwnerFixture;
import com.prgrms.catchtable.owner.service.OwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class OwnerControllerTest extends BaseIntegrationTest {

    @Autowired
    private OwnerService ownerService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final String joinEmail = "qwer@56782naver.com";
    private final String notJoinEmail = "abc1234@gmail.com";
    private final String password = "qwer1234";

    @BeforeEach
    public void init() {
        ownerService.joinOwner(OwnerFixture.getJoinOwnerRequest(joinEmail, password));
    }

    @Test
    @DisplayName("회원가입에 성공한다.")
    void joinTest() throws Exception {
        //given
        JoinOwnerRequest joinOwnerRequest = OwnerFixture.getJoinOwnerRequest(notJoinEmail,
            password);

        //then
        mockMvc.perform(post("/owners/join")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(joinOwnerRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value(joinOwnerRequest.name()))
            .andExpect(jsonPath("$.email").value(joinOwnerRequest.email()))
            .andExpect(jsonPath("$.phoneNumber").value(joinOwnerRequest.phoneNumber()))
            .andExpect(jsonPath("$.gender").value(joinOwnerRequest.gender()))
            .andExpect(jsonPath("$.dateBirth").value(joinOwnerRequest.dateBirth().toString()));
    }

    @Test
    @DisplayName("중복 이메일이 존재하여 테스트에 실패한다.")
    void joinFailureTest() throws Exception {
        //given
        JoinOwnerRequest joinOwnerRequest = OwnerFixture.getJoinOwnerRequest(joinEmail, password);

        //then
        mockMvc.perform(post("/owners/join")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(joinOwnerRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인이 정상적으로 작동한다.")
    void loginTest() throws Exception {
        //given
        LoginOwnerRequest loginOwnerRequest = OwnerFixture.getLoginOwnerRequest(joinEmail,
            password);
        Token token = jwtTokenProvider.createToken(joinEmail, Role.OWNER);

        //then
        mockMvc.perform(post("/owners/login")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(loginOwnerRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(token.getEmail()));
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인 시, 예외가 발생한다.")
    void invalidatedEmail() throws Exception {
        //given
        LoginOwnerRequest loginOwnerRequest = OwnerFixture.getLoginOwnerRequest(notJoinEmail,
            password);

        //then
        mockMvc.perform(post("/owners/login")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(loginOwnerRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("잘못된 비밀번호 입력 시, 예외가 발생한다.")
    void invalidatedPassword() throws Exception {
        //given
        LoginOwnerRequest loginOwnerRequest = OwnerFixture.getLoginOwnerRequest(notJoinEmail,
            "poiu0987");

        //then
        mockMvc.perform(post("/owners/login")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(loginOwnerRequest)))
            .andExpect(status().isBadRequest());

    }
}