package com.prgrms.catchtable.facade;

import com.prgrms.catchtable.reservation.domain.Reservation;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.reservation.dto.mapper.ReservationMapper;
import com.prgrms.catchtable.reservation.dto.request.CreateReservationRequest;
import com.prgrms.catchtable.reservation.dto.response.CreateReservationResponse;
import com.prgrms.catchtable.reservation.service.ReservationAsync;
import com.prgrms.catchtable.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationFacade {

    private final ReservationService reservationService;
    private final ReservationAsync reservationAsync;

    public CreateReservationResponse preOccupyReservation(
        CreateReservationRequest request) {
        ReservationTime reservationTime = reservationService.validateReservationAndSave(
            request);

        String shopName = reservationTime.getShop().getName();

        reservationAsync.setPreOcuppied(reservationTime);

        return CreateReservationResponse.builder()
            .shopName(shopName)
            .memberName("memberA")
            .date(reservationTime.getTime())
            .peopleCount(request.peopleCount())
            .build();
    }

    public CreateReservationResponse registerReservation(CreateReservationRequest request) {
        Reservation reservation = reservationService.validateReservationAndSaveIsEmpty(request);
        return ReservationMapper.toCreateReservationResponse(reservation);
    }

}
