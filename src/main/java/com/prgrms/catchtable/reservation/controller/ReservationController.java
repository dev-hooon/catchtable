package com.prgrms.catchtable.reservation.controller;

import com.prgrms.catchtable.facade.ReservationFacade;
import com.prgrms.catchtable.reservation.dto.request.CreateReservationRequest;
import com.prgrms.catchtable.reservation.dto.response.CreateReservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationFacade reservationFacade;

    @PostMapping
    public ResponseEntity<CreateReservationResponse> createReservationResponse(
        CreateReservationRequest request) {
        return ResponseEntity.ok(reservationFacade.preOccupyReservation(request));
    }
}
