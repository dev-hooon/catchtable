package com.prgrms.catchtable.reservation.controller;

import com.prgrms.catchtable.common.login.LogIn;
import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.reservation.dto.request.CreateReservationRequest;
import com.prgrms.catchtable.reservation.dto.request.ModifyReservationRequest;
import com.prgrms.catchtable.reservation.dto.response.CancelReservationResponse;
import com.prgrms.catchtable.reservation.dto.response.CreateReservationResponse;
import com.prgrms.catchtable.reservation.dto.response.GetAllReservationResponse;
import com.prgrms.catchtable.reservation.dto.response.ModifyReservationResponse;
import com.prgrms.catchtable.reservation.service.MemberReservationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class MemberReservationController {

    private final MemberReservationService memberReservationService;

    @PostMapping
    public ResponseEntity<CreateReservationResponse> preOccupyReservation(
        @LogIn Member member,
        @RequestBody CreateReservationRequest request) {
        return ResponseEntity.ok(memberReservationService.preOccupyReservation(member, request));
    }

    @PostMapping("/success")
    public ResponseEntity<CreateReservationResponse> registerReservation(
        @LogIn Member member,
        @RequestBody CreateReservationRequest request) {
        return ResponseEntity.ok(memberReservationService.registerReservation(member, request));
    }

    @PatchMapping("/{reservationId}")
    public ResponseEntity<ModifyReservationResponse> modifyReservation(
        @PathVariable("reservationId") Long reservationTimeId,
        @RequestBody ModifyReservationRequest request) {
        return ResponseEntity.ok(
            memberReservationService.modifyReservation(reservationTimeId, request));
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<CancelReservationResponse> cancelReservation(
        @PathVariable("reservationId") Long reservationId) {
        return ResponseEntity.ok(memberReservationService.cancelReservation(reservationId));
    }

    @GetMapping
    public ResponseEntity<List<GetAllReservationResponse>> getAllReservation(@LogIn Member member) {
        return ResponseEntity.ok(memberReservationService.getAllReservation(member));
    }
}
