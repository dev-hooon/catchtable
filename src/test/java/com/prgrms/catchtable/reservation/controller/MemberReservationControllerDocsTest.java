package com.prgrms.catchtable.reservation.controller;

import static com.prgrms.catchtable.reservation.domain.ReservationStatus.CANCELLED;
import static com.prgrms.catchtable.reservation.domain.ReservationStatus.COMPLETED;
import static org.mockito.Mockito.when;
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
import com.prgrms.catchtable.member.MemberFixture;
import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.member.repository.MemberRepository;
import com.prgrms.catchtable.reservation.dto.request.CreateReservationRequest;
import com.prgrms.catchtable.reservation.dto.request.ModifyReservationRequest;
import com.prgrms.catchtable.reservation.dto.response.CancelReservationResponse;
import com.prgrms.catchtable.reservation.dto.response.CreateReservationResponse;
import com.prgrms.catchtable.reservation.dto.response.GetAllReservationResponse;
import com.prgrms.catchtable.reservation.dto.response.ModifyReservationResponse;
import com.prgrms.catchtable.reservation.fixture.ReservationFixture;
import com.prgrms.catchtable.reservation.service.MemberReservationService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class MemberReservationControllerDocsTest extends RestDocsSupport {
    @MockBean
    private MemberReservationService memberReservationService;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp(){
        Member member = MemberFixture.member("dlswns6asd61035@gmail.com");
        Member savedMember = memberRepository.save(member);
    }



    @Test
    @DisplayName("예약 선점 api")
    void preOccupy() throws Exception {
        Member member = memberRepository.findAll().get(0);

        CreateReservationRequest request = ReservationFixture.getCreateReservationRequest();
        CreateReservationResponse response = CreateReservationResponse.builder()
            .shopName("shopA")
            .memberName(member.getName())
            .date(LocalDateTime.of(2024, 1, 1, 19, 30))
            .peopleCount(request.peopleCount())
            .build();

        when(memberReservationService.preOccupyReservation(member, request)).thenReturn(response);

        mockMvc.perform(post("/reservations")
            .headers(getHttpHeaders(member))
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(restDocs.document(
                requestFields(
                    fieldWithPath("reservationTimeId").type(NUMBER)
                        .description("예약시간 id"),
                    fieldWithPath("peopleCount").type(NUMBER)
                        .description("예약 인원")
                ),
                responseFields(
                    fieldWithPath("shopName").type(STRING)
                        .description("매장명"),
                    fieldWithPath("memberName").type(STRING)
                        .description("예약자 이름"),
                    fieldWithPath("date").type(STRING)
                        .description("예약 날짜 및 시간"),
                    fieldWithPath("peopleCount").type(NUMBER)
                        .description("예약 인원")
                )
            )
            );
    }

    @Test
    @DisplayName("예약 등록 api")
    void register() throws Exception {
        Member member = memberRepository.findAll().get(0);

        CreateReservationRequest request = ReservationFixture.getCreateReservationRequest();
        CreateReservationResponse response = CreateReservationResponse.builder()
            .shopName("shopA")
            .memberName(member.getName())
            .date(LocalDateTime.of(2024, 1, 1, 19, 30))
            .peopleCount(request.peopleCount())
            .build();

        when(memberReservationService.registerReservation(member, request)).thenReturn(response);

        mockMvc.perform(post("/reservations/success")
                .headers(getHttpHeaders(member))
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(restDocs.document(
                    requestFields(
                        fieldWithPath("reservationTimeId").type(NUMBER)
                            .description("예약시간 id"),
                        fieldWithPath("peopleCount").type(NUMBER)
                            .description("예약 인원")
                    ),
                    responseFields(
                        fieldWithPath("shopName").type(STRING)
                            .description("매장명"),
                        fieldWithPath("memberName").type(STRING)
                            .description("예약자 이름"),
                        fieldWithPath("date").type(STRING)
                            .description("예약 날짜 및 시간"),
                        fieldWithPath("peopleCount").type(NUMBER)
                            .description("예약 인원")
                    )
                )
            );
    }

    @Test
    @DisplayName("예약 전체 조회 api")
    void getAll() throws Exception {
        Member member = memberRepository.findAll().get(0);
        GetAllReservationResponse reservation1 = GetAllReservationResponse.builder()
            .reservationId(1L)
            .date(LocalDateTime.of(2024, 1, 1, 19, 30))
            .shopName("shopA")
            .peopleCount(5)
            .status(COMPLETED)
            .build();
        GetAllReservationResponse reservation2 = GetAllReservationResponse.builder()
            .reservationId(2L)
            .date(LocalDateTime.of(2024, 1, 5, 20, 30))
            .shopName("shopB")
            .peopleCount(3)
            .status(CANCELLED)
            .build();

        List<GetAllReservationResponse> response = List.of(reservation1, reservation2);

        when(memberReservationService.getAllReservation(member)).thenReturn(response);

        mockMvc.perform(get("/reservations")
            .headers(getHttpHeaders(member)))
            .andExpect(status().isOk())
            .andDo(restDocs.document(
                responseFields(
                    fieldWithPath("[0].reservationId").type(NUMBER)
                        .description("예약 id"),
                    fieldWithPath("[0].date").type(STRING)
                        .description("예약 날짜 및 시간"),
                    fieldWithPath("[0].shopName").type(STRING)
                        .description("매장명"),
                    fieldWithPath("[0].peopleCount").type(NUMBER)
                        .description("예약 인원 수"),
                    fieldWithPath("[0].status").type(STRING)
                            .description("예약 상태"),
                    fieldWithPath("[1].reservationId").type(NUMBER)
                        .description("예약 id"),
                    fieldWithPath("[1].date").type(STRING)
                        .description("예약 날짜 및 시간"),
                    fieldWithPath("[1].shopName").type(STRING)
                        .description("매장명"),
                    fieldWithPath("[1].peopleCount").type(NUMBER)
                        .description("예약 인원 수"),
                    fieldWithPath("[1].status").type(STRING)
                        .description("예약 상태")
                )
            ));
    }

    @Test
    @DisplayName("예약 수정 api")
    void modify() throws Exception {
        Member member = memberRepository.findAll().get(0);

        ModifyReservationRequest request = ReservationFixture.getModifyReservationRequest(1L);

        ModifyReservationResponse response = ModifyReservationResponse.builder()
            .shopName("shopA")
            .memberName(member.getName())
            .date(LocalDateTime.of(2024, 1, 1, 19, 30))
            .peopleCount(5)
            .build();

        when(memberReservationService.modifyReservation(1L, request)).thenReturn(response);

        mockMvc.perform(patch("/reservations/{reservationId}", 1)
            .contentType(APPLICATION_JSON)
            .headers(getHttpHeaders(member))
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(restDocs.document(
                requestFields(
                    fieldWithPath("reservationTimeId").type(NUMBER)
                        .description("수정하려는 예약시간 id"),
                    fieldWithPath("peopleCount").type(NUMBER)
                        .description("수정하려는 인원 수")
                ),
                responseFields(
                    fieldWithPath("shopName").type(STRING)
                        .description("매장명"),
                    fieldWithPath("memberName").type(STRING)
                        .description("예약자명"),
                    fieldWithPath("date").type(STRING)
                        .description("수정된 예약 날짜 및 시간"),
                    fieldWithPath("peopleCount").type(NUMBER)
                        .description("수정된 예약 인원 수")
                )
            ));
    }

    @Test
    @DisplayName("예약 취소 api")
    void cancel() throws Exception {
        Member member = memberRepository.findAll().get(0);

        CancelReservationResponse response = CancelReservationResponse.builder()
            .status(CANCELLED)
            .build();

        when(memberReservationService.cancelReservation(member, 1L)).thenReturn(response);

        mockMvc.perform(delete("/reservations/{reservationId}", 1L)
            .contentType(APPLICATION_JSON)
            .headers(getHttpHeaders(member)))
            .andExpect(status().isOk())
            .andDo(restDocs.document(
                responseFields(
                    fieldWithPath("status").type(STRING)
                        .description("예약 상태")
                )
            ));
    }

}
