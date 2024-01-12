package com.prgrms.catchtable.waiting.controller;

import com.prgrms.catchtable.common.login.LogIn;
import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.waiting.dto.response.OwnerWaitingListResponse;
import com.prgrms.catchtable.waiting.dto.response.OwnerWaitingResponse;
import com.prgrms.catchtable.waiting.service.OwnerWaitingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("owner/waitings")
@RestController
public class OwnerWaitingController {

    private final OwnerWaitingService ownerWaitingService;

    @GetMapping
    public ResponseEntity<OwnerWaitingListResponse> getShopAllWaiting(
        @LogIn Owner owner) {
        OwnerWaitingListResponse response = ownerWaitingService.getShopAllWaiting(owner);
        return ResponseEntity.ok(response);
    }

    @PatchMapping
    public ResponseEntity<OwnerWaitingResponse> entryWaiting(
        @LogIn Owner owner) {
        OwnerWaitingResponse response = ownerWaitingService.entryWaiting(owner);
        return ResponseEntity.ok(response);
    }
}
