package com.prgrms.catchtable.facade;

import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.reservation.dto.request.CreateReservationRequest;
import com.prgrms.catchtable.reservation.dto.response.CreateReservationResponse;
import com.prgrms.catchtable.reservation.dto.response.ValidateReservationResponse;
import com.prgrms.catchtable.reservation.service.ReservationAsync;
import com.prgrms.catchtable.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationFacade {

    private final ReservationService reservationService;
    private final ReservationAsync reservationAsync;

    public CreateReservationResponse createReservation(Long shopId,
        CreateReservationRequest request) {
        ValidateReservationResponse validateReservationResponse = reservationService.validateReservationIsPossible(
            shopId,
            request);

        String shopName = validateReservationResponse.shopName();
        ReservationTime reservationTime = validateReservationResponse.reservationTime();

        reservationAsync.setPreOcuppied(reservationTime);

        return new CreateReservationResponse(shopName, "memberA", reservationTime.getTime(),
            request.peopleCount());
    }

}
