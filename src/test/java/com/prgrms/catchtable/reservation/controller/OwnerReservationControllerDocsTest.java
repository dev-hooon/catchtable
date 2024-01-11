package com.prgrms.catchtable.reservation.controller;

import static com.prgrms.catchtable.reservation.domain.ReservationStatus.CANCELLED;
import static com.prgrms.catchtable.reservation.domain.ReservationStatus.COMPLETED;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.prgrms.catchtable.common.restdocs.RestDocsSupport;
import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.owner.fixture.OwnerFixture;
import com.prgrms.catchtable.owner.repository.OwnerRepository;
import com.prgrms.catchtable.reservation.dto.request.ModifyReservationStatusRequest;
import com.prgrms.catchtable.reservation.dto.response.OwnerGetAllReservationResponse;
import com.prgrms.catchtable.reservation.fixture.ReservationFixture;
import com.prgrms.catchtable.reservation.service.OwnerReservationService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class OwnerReservationControllerDocsTest extends RestDocsSupport {
    @MockBean
    private OwnerReservationService ownerReservationService;
    @Autowired
    private OwnerRepository ownerRepository;

    @BeforeEach
    void setUp(){
        Owner owner = OwnerFixture.getOwner("dlswns", "dlswns24802840");
        ownerRepository.save(owner);
    }

    @Test
    @DisplayName("예약 노쇼, 취소 처리 api")
    void noshowAndCancel() throws Exception {
        Owner owner = ownerRepository.findAll().get(0);

        ModifyReservationStatusRequest request = ReservationFixture.getModifyReservationStatusRequest(
            CANCELLED);
        doNothing().when(ownerReservationService).modifyReservationStatus(1L, request);

        mockMvc.perform(patch("/owners/shop/{reservationId}", 1)
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .headers(getHttpHeaders(owner)))
            .andExpect(status().isOk())
            .andDo(restDocs.document(
                requestFields(
                    fieldWithPath("status").type(STRING)
                        .description("수정하려는 예약 상태")
                )
            ));
    }

    @Test
    @DisplayName("가게의 예약 전체 조회 api")
    void getAllReservation() throws Exception {
        Owner owner = ownerRepository.findAll().get(0);
        OwnerGetAllReservationResponse reservation1 = OwnerGetAllReservationResponse.builder()
            .reservationId(1L)
            .date(LocalDateTime.of(2024, 3, 4, 12, 30))
            .peopleCount(4)
            .status(COMPLETED)
            .build();
        OwnerGetAllReservationResponse reservation2 = OwnerGetAllReservationResponse.builder()
            .reservationId(2L)
            .date(LocalDateTime.of(2024, 3, 25, 17, 30))
            .peopleCount(2)
            .status(CANCELLED)
            .build();
        List<OwnerGetAllReservationResponse> response = List.of(reservation1, reservation2);

        Mockito.when(ownerReservationService.getAllReservation(owner)).thenReturn(response);

        mockMvc.perform(get("/owners/shop")
            .headers(getHttpHeaders(owner)))
            .andExpect(status().isOk())
            .andDo(restDocs.document(
                responseFields(
                    fieldWithPath("[0].reservationId").type(NUMBER)
                        .description("예약 id"),
                    fieldWithPath("[0].date").type(STRING)
                        .description("예약 날짜 및 시간"),
                    fieldWithPath("[0].peopleCount").type(NUMBER)
                        .description("예약 인원 수"),
                    fieldWithPath("[0].status").type(STRING)
                        .description("예약 상태"),
                    fieldWithPath("[1].reservationId").type(NUMBER)
                        .description("예약 id"),
                    fieldWithPath("[1].date").type(STRING)
                        .description("예약 날짜 및 시간"),
                    fieldWithPath("[1].peopleCount").type(NUMBER)
                        .description("예약 인원 수"),
                    fieldWithPath("[1].status").type(STRING)
                        .description("예약 상태")
                )
            ));
    }
}
