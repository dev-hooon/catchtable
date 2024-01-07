package com.prgrms.catchtable.waiting.controller;

import com.prgrms.catchtable.waiting.dto.CreateWaitingRequest;
import com.prgrms.catchtable.waiting.dto.WaitingResponse;
import com.prgrms.catchtable.waiting.service.MemberWaitingService;
import jakarta.validation.Valid;
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

@RequiredArgsConstructor
@RequestMapping("/waitings")
@RestController
public class MemberWaitingController {

    private final MemberWaitingService memberWaitingService;

    @PostMapping("/{shopId}/{memberId}")
    public ResponseEntity<WaitingResponse> createWaiting(@PathVariable("shopId") Long shopId,
        @PathVariable("memberId") Long memberId,
        @Valid @RequestBody CreateWaitingRequest request) {
        WaitingResponse response = memberWaitingService.createWaiting(shopId, memberId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<WaitingResponse> postponeWaiting(
        @PathVariable("memberId") Long memberId) {
        WaitingResponse response = memberWaitingService.postponeWaiting(memberId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<WaitingResponse> cancelWaiting(@PathVariable("memberId") Long memberId) {
        WaitingResponse response = memberWaitingService.cancelWaiting(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<WaitingResponse> getWaiting(@PathVariable("memberId") Long memberId) {
        WaitingResponse response = memberWaitingService.getWaiting(memberId);
        return ResponseEntity.ok(response);
    }
}
