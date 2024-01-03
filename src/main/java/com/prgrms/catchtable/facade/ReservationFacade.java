package com.prgrms.catchtable.facade;

import static com.prgrms.catchtable.reservation.dto.mapper.ReservationMapper.toCreateReservationResponse;

import com.prgrms.catchtable.reservation.domain.Reservation;
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


    public CreateReservationResponse registerReservation(CreateReservationRequest request) {
        Reservation reservation = reservationService.validateReservationAndSaveIsEmpty(request);
        return toCreateReservationResponse(reservation);
    }

}
