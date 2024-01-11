package com.prgrms.catchtable.reservation.controller;

import com.prgrms.catchtable.common.login.LogIn;
import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.reservation.dto.request.ModifyReservationStatusRequest;
import com.prgrms.catchtable.reservation.dto.response.OwnerGetAllReservationResponse;
import com.prgrms.catchtable.reservation.service.OwnerReservationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/owners/shop")
@RequiredArgsConstructor
public class OwnerReservationController {

    private final OwnerReservationService ownerReservationService;

    @PatchMapping("/{reservationId}")
    public void modifyReservationStatus(
        @PathVariable("reservationId") Long reservationId,
        @RequestBody ModifyReservationStatusRequest request
    ) {
        ownerReservationService.modifyReservationStatus(reservationId, request);
    }

    @GetMapping
    public ResponseEntity<List<OwnerGetAllReservationResponse>> getAllReservation(
        @LogIn Owner owner) {
        return ResponseEntity.ok(ownerReservationService.getAllReservation(owner));
    }
}
