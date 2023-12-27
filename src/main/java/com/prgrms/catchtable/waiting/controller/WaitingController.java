package com.prgrms.catchtable.waiting.controller;

import com.prgrms.catchtable.waiting.dto.CreateWaitingRequest;
import com.prgrms.catchtable.waiting.dto.CreateWaitingResponse;
import com.prgrms.catchtable.waiting.service.WaitingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/{shopId}")
    public ResponseEntity<CreateWaitingResponse> createWaiting(@PathVariable("shopId") Long shopId,
        @RequestBody CreateWaitingRequest request) {
        CreateWaitingResponse response = waitingService.createWaiting(shopId, request);
        return ResponseEntity.ok(response);
    }
}
