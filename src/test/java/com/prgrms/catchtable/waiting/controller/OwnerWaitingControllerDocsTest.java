package com.prgrms.catchtable.waiting.controller;

import static com.prgrms.catchtable.waiting.domain.WaitingStatus.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.prgrms.catchtable.common.restdocs.RestDocsSupport;
import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.owner.fixture.OwnerFixture;
import com.prgrms.catchtable.owner.repository.OwnerRepository;
import com.prgrms.catchtable.waiting.domain.WaitingStatus;
import com.prgrms.catchtable.waiting.dto.response.OwnerWaitingListResponse;
import com.prgrms.catchtable.waiting.dto.response.OwnerWaitingResponse;
import com.prgrms.catchtable.waiting.service.OwnerWaitingService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class OwnerWaitingControllerDocsTest extends RestDocsSupport {

    @MockBean
    private OwnerWaitingService ownerWaitingService;

    @Autowired
    private OwnerRepository ownerRepository;

    private Owner owner;

    @BeforeEach
    void setUp() {
        owner = OwnerFixture.getOwner("hyun@gmail.com", "hyun1234");
        ownerRepository.save(owner);
    }

    @DisplayName("웨이팅 입장 API")
    @Test
    void entryWaiting() throws Exception {
        //given
        OwnerWaitingResponse response = OwnerWaitingResponse.builder()
            .waitingId(1L)
            .waitingNumber(20)
            .rank(0L)
            .peopleCount(2)
            .status(COMPLETED.getDescription())
            .build();
        given(ownerWaitingService.entryWaiting(owner)).willReturn(response);
        //when, then
        mockMvc.perform(patch("/owner/waitings")
                .contentType(APPLICATION_JSON)
                .headers(getHttpHeaders(owner)))
            .andExpect(status().isOk())
            .andDo(restDocs.document(
                responseFields(
                    fieldWithPath("waitingId").type(NUMBER)
                        .description("회원 웨이팅 아이디"),
                    fieldWithPath("waitingNumber").type(NUMBER)
                        .description("웨이팅 고유 번호"),
                    fieldWithPath("rank").type(NUMBER)
                        .description("웨이팅 순서"),
                    fieldWithPath("peopleCount").type(NUMBER)
                        .description("인원 수"),
                    fieldWithPath("status").type(STRING)
                        .description("대기 상태")
                )
            ));

    }

    @DisplayName("owner 가게의 웨이팅 목록 조회")
    @Test
    void getShopAllWaiting() throws Exception {
        //given
        OwnerWaitingResponse response1 = OwnerWaitingResponse.builder()
            .waitingId(1L)
            .waitingNumber(20)
            .rank(1L)
            .peopleCount(2)
            .status(PROGRESS.getDescription())
            .build();
        OwnerWaitingResponse response2 = OwnerWaitingResponse.builder()
            .waitingId(2L)
            .waitingNumber(21)
            .rank(2L)
            .peopleCount(2)
            .status(PROGRESS.getDescription())
            .build();
        OwnerWaitingListResponse responses = OwnerWaitingListResponse.builder()
            .shopWaitings(List.of(response1, response2))
            .build();

        given(ownerWaitingService.getShopAllWaiting(owner)).willReturn(responses);

        //when, then
        mockMvc.perform(get("/owner/waitings")
                .contentType(APPLICATION_JSON)
                .headers(getHttpHeaders(owner)))
            .andExpect(status().isOk())
            .andDo(restDocs.document(
                responseFields(
                    fieldWithPath("shopWaitings[0].waitingId").type(NUMBER)
                        .description("회원 웨이팅 아이디"),
                    fieldWithPath("shopWaitings[0].waitingNumber").type(NUMBER)
                        .description("웨이팅 고유 번호"),
                    fieldWithPath("shopWaitings[0].rank").type(NUMBER)
                        .description("웨이팅 순서"),
                    fieldWithPath("shopWaitings[0].peopleCount").type(NUMBER)
                        .description("인원 수"),
                    fieldWithPath("shopWaitings[0].status").type(STRING)
                        .description("대기 상태"),
                    fieldWithPath("shopWaitings[1].waitingId").type(NUMBER)
                        .description("회원 웨이팅 아이디"),
                    fieldWithPath("shopWaitings[1].waitingNumber").type(NUMBER)
                        .description("웨이팅 고유 번호"),
                    fieldWithPath("shopWaitings[1].rank").type(NUMBER)
                        .description("웨이팅 순서"),
                    fieldWithPath("shopWaitings[1].peopleCount").type(NUMBER)
                        .description("인원 수"),
                    fieldWithPath("shopWaitings[1].status").type(STRING)
                        .description("대기 상태")
                )
            ));
    }
}
