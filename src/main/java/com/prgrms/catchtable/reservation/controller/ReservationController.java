package com.prgrms.catchtable.reservation.controller;

import com.prgrms.catchtable.reservation.dto.request.CreateReservationRequest;
import com.prgrms.catchtable.reservation.dto.request.ModifyReservationRequest;
import com.prgrms.catchtable.reservation.dto.response.CreateReservationResponse;
import com.prgrms.catchtable.reservation.dto.response.ModifyReservationResponse;
import com.prgrms.catchtable.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<CreateReservationResponse> preOccupyReservation(
        @RequestBody CreateReservationRequest request) {
        return ResponseEntity.ok(reservationService.preOccupyReservation(request));
    }

    @PostMapping("/success")
    public ResponseEntity<CreateReservationResponse> registerReservation(
        @RequestBody CreateReservationRequest request) {
        return ResponseEntity.ok(reservationService.registerReservation(request));
    }

    @PatchMapping("/{reservationId}")
    public ResponseEntity<ModifyReservationResponse> modifyReservation(
        @PathVariable("reservationId") Long reservationTimeId,
        @RequestBody ModifyReservationRequest request) {
        return ResponseEntity.ok(reservationService.modifyReservation(reservationTimeId, request));
    }
}
