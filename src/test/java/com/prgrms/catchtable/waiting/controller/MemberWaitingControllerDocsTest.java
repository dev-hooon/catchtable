package com.prgrms.catchtable.waiting.controller;

import static com.prgrms.catchtable.member.MemberFixture.member;
import static com.prgrms.catchtable.waiting.domain.WaitingStatus.CANCELED;
import static com.prgrms.catchtable.waiting.domain.WaitingStatus.PROGRESS;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.prgrms.catchtable.common.restdocs.RestDocsSupport;
import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.member.repository.MemberRepository;
import com.prgrms.catchtable.waiting.dto.request.CreateWaitingRequest;
import com.prgrms.catchtable.waiting.dto.response.MemberWaitingHistoryListResponse;
import com.prgrms.catchtable.waiting.dto.response.MemberWaitingHistoryResponse;
import com.prgrms.catchtable.waiting.dto.response.MemberWaitingResponse;
import com.prgrms.catchtable.waiting.fixture.WaitingFixture;
import com.prgrms.catchtable.waiting.service.MemberWaitingService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class MemberWaitingControllerDocsTest extends RestDocsSupport {

    @MockBean
    private MemberWaitingService memberWaitingService;
    @Autowired
    private MemberRepository memberRepository;
    private Member member;

    @BeforeEach
    void setUp() {
        member = member("test@naver.com");
        memberRepository.save(member);
    }

    @DisplayName("웨이팅 생성 API")
    @Test
    void createWaiting() throws Exception {
        CreateWaitingRequest request = CreateWaitingRequest
            .builder()
            .peopleCount(2).build();
        MemberWaitingResponse response = WaitingFixture.memberWaitingResponse(2, PROGRESS);
        given(memberWaitingService.createWaiting(1L, member, request)).willReturn(response);

        mockMvc.perform(post("/waitings/{shopId}", 1)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .headers(getHttpHeaders(member)))
            .andExpect(status().isOk())
            .andDo(restDocs.document(
                requestFields(
                    fieldWithPath("peopleCount").type(NUMBER)
                        .description("인원수")
                ),
                responseFields(
                    fieldWithPath("waitingId").type(NUMBER)
                        .description("생성된 웨이팅 아이디"),
                    fieldWithPath("shopId").type(NUMBER)
                        .description("상점 아이디"),
                    fieldWithPath("shopName").type(STRING)
                        .description("상점 이름"),
                    fieldWithPath("peopleCount").type(NUMBER)
                        .description("인원 수"),
                    fieldWithPath("waitingNumber").type(NUMBER)
                        .description("웨이팅 고유 번호"),
                    fieldWithPath("rank").type(NUMBER)
                        .description("웨이팅 순서"),
                    fieldWithPath("remainingPostponeCount").type(NUMBER)
                        .description("대기 지연 잔여 횟수"),
                    fieldWithPath("status").type(STRING)
                        .description("대기 상태")
                )
            ));
    }

    @DisplayName("웨이팅 지연 API")
    @Test
    void postponeWaiting() throws Exception {
        //given
        MemberWaitingResponse response = WaitingFixture.memberWaitingResponse(2, PROGRESS);
        given(memberWaitingService.postponeWaiting(member)).willReturn(response);
        //when, then
        mockMvc.perform(patch("/waitings")
                .contentType(APPLICATION_JSON)
                .headers(getHttpHeaders(member)))
            .andExpect(status().isOk())
            .andDo(restDocs.document(
                responseFields(
                    fieldWithPath("waitingId").type(NUMBER)
                        .description("생성된 웨이팅 아이디"),
                    fieldWithPath("shopId").type(NUMBER)
                        .description("상점 아이디"),
                    fieldWithPath("shopName").type(STRING)
                        .description("상점 이름"),
                    fieldWithPath("peopleCount").type(NUMBER)
                        .description("인원 수"),
                    fieldWithPath("waitingNumber").type(NUMBER)
                        .description("웨이팅 고유 번호"),
                    fieldWithPath("rank").type(NUMBER)
                        .description("웨이팅 순서"),
                    fieldWithPath("remainingPostponeCount").type(NUMBER)
                        .description("대기 지연 잔여 횟수"),
                    fieldWithPath("status").type(STRING)
                        .description("대기 상태")
                )
            ));
    }

    @DisplayName("웨이팅 취소 API")
    @Test
    void cancelWaiting() throws Exception {
        //given
        MemberWaitingResponse response = WaitingFixture.memberWaitingResponse(1,
            CANCELED);

        given(memberWaitingService.cancelWaiting(member)).willReturn(response);
        //when, then
        mockMvc.perform(delete("/waitings")
                .contentType(APPLICATION_JSON)
                .headers(getHttpHeaders(member)))
            .andExpect(status().isOk())
            .andDo(restDocs.document(
                responseFields(
                    fieldWithPath("waitingId").type(NUMBER)
                        .description("생성된 웨이팅 아이디"),
                    fieldWithPath("shopId").type(NUMBER)
                        .description("상점 아이디"),
                    fieldWithPath("shopName").type(STRING)
                        .description("상점 이름"),
                    fieldWithPath("peopleCount").type(NUMBER)
                        .description("인원 수"),
                    fieldWithPath("waitingNumber").type(NUMBER)
                        .description("웨이팅 고유 번호"),
                    fieldWithPath("rank").type(NUMBER)
                        .description("웨이팅 순서"),
                    fieldWithPath("remainingPostponeCount").type(NUMBER)
                        .description("대기 지연 잔여 횟수"),
                    fieldWithPath("status").type(STRING)
                        .description("대기 상태")
                )
            ));
    }

    @DisplayName("회원 진행 중인 웨이팅 조회 API")
    @Test
    void getWaiting() throws Exception {
        //given
        MemberWaitingResponse response =
            WaitingFixture.memberWaitingResponse(2,
            PROGRESS);
        given(memberWaitingService.getWaiting(member)).willReturn(response);
        //when, then
        mockMvc.perform(get("/waitings")
                .contentType(APPLICATION_JSON)
                .headers(getHttpHeaders(member)))
            .andExpect(status().isOk())
            .andDo(restDocs.document(
                responseFields(
                    fieldWithPath("waitingId").type(NUMBER)
                        .description("생성된 웨이팅 아이디"),
                    fieldWithPath("shopId").type(NUMBER)
                        .description("상점 아이디"),
                    fieldWithPath("shopName").type(STRING)
                        .description("상점 이름"),
                    fieldWithPath("peopleCount").type(NUMBER)
                        .description("인원 수"),
                    fieldWithPath("waitingNumber").type(NUMBER)
                        .description("웨이팅 고유 번호"),
                    fieldWithPath("rank").type(NUMBER)
                        .description("웨이팅 순서"),
                    fieldWithPath("remainingPostponeCount").type(NUMBER)
                        .description("대기 지연 잔여 횟수"),
                    fieldWithPath("status").type(STRING)
                        .description("대기 상태")
                )
            ));
    }

    @DisplayName("회원의 웨이팅 이력 조회")
    @Test
    void getMemberWaitingHistory() throws Exception {
        //given
        MemberWaitingHistoryResponse response1 = MemberWaitingHistoryResponse.builder()
            .waitingId(1L)
            .shopId(77L)
            .shopName("shop1")
            .status("취소")
            .peopleCount(2)
            .build();
        MemberWaitingHistoryResponse response2 = MemberWaitingHistoryResponse.builder()
            .waitingId(22L)
            .shopId(77L)
            .shopName("shop1")
            .status("진행중")
            .peopleCount(2)
            .build();
        MemberWaitingHistoryListResponse responses = MemberWaitingHistoryListResponse.builder()
            .memberWaitings(List.of(response1, response2))
            .build();
        given(memberWaitingService.getMemberWaitingHistory(member)).willReturn(responses);
        //when, then
        mockMvc.perform(get("/waitings/all")
                .contentType(APPLICATION_JSON)
                .headers(getHttpHeaders(member)))
            .andExpect(status().isOk())
            .andDo(restDocs.document(
                responseFields(
                    fieldWithPath("memberWaitings[0].waitingId").type(NUMBER)
                        .description("회원 웨이팅 아이디"),
                    fieldWithPath("memberWaitings[0].shopId").type(NUMBER)
                        .description("가게 아이디"),
                    fieldWithPath("memberWaitings[0].shopName").type(STRING)
                        .description("가게 이름"),
                    fieldWithPath("memberWaitings[0].peopleCount").type(NUMBER)
                        .description("인원 수"),
                    fieldWithPath("memberWaitings[0].status").type(STRING)
                        .description("대기 상태"),
                    fieldWithPath("memberWaitings[1].waitingId").type(NUMBER)
                        .description("회원 웨이팅 아이디"),
                    fieldWithPath("memberWaitings[1].shopId").type(NUMBER)
                        .description("가게 아이디"),
                    fieldWithPath("memberWaitings[1].shopName").type(STRING)
                        .description("가게 이름"),
                    fieldWithPath("memberWaitings[1].peopleCount").type(NUMBER)
                        .description("인원 수"),
                    fieldWithPath("memberWaitings[1].status").type(STRING)
                        .description("대기 상태")
                )
            ));
    }


}