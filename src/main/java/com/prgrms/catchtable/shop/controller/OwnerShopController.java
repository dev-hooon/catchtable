package com.prgrms.catchtable.shop.controller;

import com.prgrms.catchtable.common.login.LogIn;
import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.shop.dto.request.RegistShopRequest;
import com.prgrms.catchtable.shop.dto.response.RegistShopResponse;
import com.prgrms.catchtable.shop.service.OwnerShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/owners")
public class OwnerShopController {

    private final OwnerShopService shopService;

    @PostMapping("/shops")
    public ResponseEntity<RegistShopResponse> registShop(
        @Valid @RequestBody RegistShopRequest request, @LogIn
    Owner owner) {
        RegistShopResponse registShopResponse = shopService.registShop(request, owner);
        return ResponseEntity.status(HttpStatus.CREATED).body(registShopResponse);
    }

}
