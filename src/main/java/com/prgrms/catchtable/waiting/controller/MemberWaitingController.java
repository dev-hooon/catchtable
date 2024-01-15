package com.prgrms.catchtable.waiting.controller;

import com.prgrms.catchtable.common.login.LogIn;
import com.prgrms.catchtable.member.domain.Member;
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

    @PostMapping("/{shopId}")
    public ResponseEntity<MemberWaitingResponse> createWaiting(@PathVariable("shopId") Long shopId,
        @LogIn Member member,
        @Valid @RequestBody CreateWaitingRequest request) {
        MemberWaitingResponse response = memberWaitingService.createWaiting(shopId, member,
            request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping
    public ResponseEntity<MemberWaitingResponse> postponeWaiting(
        @LogIn Member member) {
        MemberWaitingResponse response = memberWaitingService.postponeWaiting(member);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<MemberWaitingResponse> cancelWaiting(
        @LogIn Member member) {
        MemberWaitingResponse response = memberWaitingService.cancelWaiting(member);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<MemberWaitingResponse> getWaiting(
        @LogIn Member member) {
        MemberWaitingResponse response = memberWaitingService.getWaiting(member);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<MemberWaitingHistoryListResponse> getWaitingHistory(
        @LogIn Member member) {
        MemberWaitingHistoryListResponse response = memberWaitingService.getWaitingHistory(
            member);
        return ResponseEntity.ok(response);
    }
}
