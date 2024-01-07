package com.prgrms.catchtable.waiting.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.prgrms.catchtable.common.restdocs.RestDocsSupport;
import com.prgrms.catchtable.waiting.dto.CreateWaitingRequest;
import com.prgrms.catchtable.waiting.dto.WaitingResponse;
import com.prgrms.catchtable.waiting.service.MemberWaitingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

class MemberWaitingControllerDocsTest extends RestDocsSupport {

    private final MemberWaitingService memberWaitingService = mock(MemberWaitingService.class);

    @Override
    protected Object initController() {
        return new MemberWaitingController(memberWaitingService);
    }

    @DisplayName("웨이팅 생성 API")
    @Test
    void createWaiting() throws Exception {
        CreateWaitingRequest request = CreateWaitingRequest
            .builder()
            .peopleCount(2).build();
        WaitingResponse response = WaitingResponse.builder()
            .waitingId(201L)
            .shopId(1L)
            .shopName("shop1")
            .waitingNumber(324)
            .rank(20L)
            .peopleCount(2)
            .remainingPostponeCount(2)
            .status("진행 중")
            .build();

        given(memberWaitingService.createWaiting(1L, 1L, request)).willReturn(response);

        mockMvc.perform(post("/waitings/{shopId}/{memberId}", 1, 1)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andDo(document("waiting-create",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("peopleCount").type(JsonFieldType.NUMBER)
                        .description("인원수")
                ),
                responseFields(
                    fieldWithPath("waitingId").type(JsonFieldType.NUMBER)
                        .description("생성된 웨이팅 아이디"),
                    fieldWithPath("shopId").type(JsonFieldType.NUMBER)
                        .description("상점 아이디"),
                    fieldWithPath("shopName").type(JsonFieldType.STRING)
                        .description("상점 이름"),
                    fieldWithPath("peopleCount").type(JsonFieldType.NUMBER)
                        .description("인원 수"),
                    fieldWithPath("waitingNumber").type(JsonFieldType.NUMBER)
                        .description("웨이팅 고유 번호"),
                    fieldWithPath("rank").type(JsonFieldType.NUMBER)
                        .description("웨이팅 순서"),
                    fieldWithPath("remainingPostponeCount").type(JsonFieldType.NUMBER)
                        .description("대기 지연 잔여 횟수"),
                    fieldWithPath("status").type(JsonFieldType.STRING)
                        .description("대기 상태")
                )
            ));


    }
}