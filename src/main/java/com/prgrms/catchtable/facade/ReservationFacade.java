package com.prgrms.catchtable.facade;

import com.prgrms.catchtable.reservation.domain.ReservationTime;
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

        return new CreateReservationResponse(shopName, "memberA", reservationTime.getTime(),
            request.peopleCount());
    }

}
