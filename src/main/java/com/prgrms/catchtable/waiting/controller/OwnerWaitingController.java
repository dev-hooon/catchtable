package com.prgrms.catchtable.waiting.controller;

import com.prgrms.catchtable.waiting.dto.response.OwnerWaitingListResponse;
import com.prgrms.catchtable.waiting.dto.response.OwnerWaitingResponse;
import com.prgrms.catchtable.waiting.service.OwnerWaitingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("owner/waitings")
@RestController
public class OwnerWaitingController {

    private final OwnerWaitingService ownerWaitingService;

    @GetMapping("/{ownerId}")
    public ResponseEntity<OwnerWaitingListResponse> getOwnerAllWaiting(
        @PathVariable("ownerId") Long ownerId) {
        OwnerWaitingListResponse response = ownerWaitingService.getOwnerAllWaiting(ownerId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{ownerId}")
    public ResponseEntity<OwnerWaitingResponse> entryWaiting(
        @PathVariable("ownerId") Long ownerId) {
        OwnerWaitingResponse response = ownerWaitingService.entryWaiting(ownerId);
        return ResponseEntity.ok(response);
    }
}
