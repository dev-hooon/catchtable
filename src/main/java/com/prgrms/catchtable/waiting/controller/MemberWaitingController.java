package com.prgrms.catchtable.waiting.controller;

import com.prgrms.catchtable.waiting.dto.request.CreateWaitingRequest;
import com.prgrms.catchtable.waiting.dto.response.MemberWaitingHistoryListResponse;
import com.prgrms.catchtable.waiting.dto.response.MemberWaitingResponse;
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
    public ResponseEntity<MemberWaitingResponse> createWaiting(@PathVariable("shopId") Long shopId,
        @PathVariable("memberId") Long memberId,
        @Valid @RequestBody CreateWaitingRequest request) {
        MemberWaitingResponse response = memberWaitingService.createWaiting(shopId, memberId,
            request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<MemberWaitingResponse> postponeWaiting(
        @PathVariable("memberId") Long memberId) {
        MemberWaitingResponse response = memberWaitingService.postponeWaiting(memberId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<MemberWaitingResponse> cancelWaiting(
        @PathVariable("memberId") Long memberId) {
        MemberWaitingResponse response = memberWaitingService.cancelWaiting(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberWaitingResponse> getWaiting(
        @PathVariable("memberId") Long memberId) {
        MemberWaitingResponse response = memberWaitingService.getWaiting(memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all/{memberId}")
    public ResponseEntity<MemberWaitingHistoryListResponse> getMemberAllWaiting(
        @PathVariable("memberId") Long memberId) {
        MemberWaitingHistoryListResponse response = memberWaitingService.getMemberAllWaiting(memberId);
        return ResponseEntity.ok(response);
    }
}
