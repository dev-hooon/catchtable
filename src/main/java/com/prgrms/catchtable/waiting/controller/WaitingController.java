package com.prgrms.catchtable.waiting.controller;

import com.prgrms.catchtable.waiting.dto.CreateWaitingRequest;
import com.prgrms.catchtable.waiting.dto.WaitingResponse;
import com.prgrms.catchtable.waiting.service.WaitingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/waitings")
@RestController
public class WaitingController {

    private final WaitingService waitingService;

    @PostMapping("/{shopId}/{memberId}")
    public ResponseEntity<WaitingResponse> createWaiting(@PathVariable("shopId") Long shopId,
        @PathVariable("memberId") Long memberId,
        @Valid @RequestBody CreateWaitingRequest request) {
        WaitingResponse response = waitingService.createWaiting(shopId, memberId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<WaitingResponse> postponeWaiting(
        @PathVariable("memberId") Long memberId) {
        WaitingResponse response = waitingService.postponeWaiting(memberId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<WaitingResponse> cancelWaiting(@PathVariable Long memberId) {
        WaitingResponse response = waitingService.cancelWaiting(memberId);
        return ResponseEntity.ok(response);
    }
}
