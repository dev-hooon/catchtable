package com.prgrms.catchtable.shop.controller;

import com.prgrms.catchtable.shop.dto.request.ShopSearchCondition;
import com.prgrms.catchtable.shop.dto.response.GetAllShopResponses;
import com.prgrms.catchtable.shop.dto.response.GetShopResponse;
import com.prgrms.catchtable.shop.service.MemberShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shops")
public class MemberShopController {

    private final MemberShopService memberShopService;

    @GetMapping
    public ResponseEntity<GetAllShopResponses> getAll() {
        return ResponseEntity.ok(memberShopService.getAll());
    }

    @GetMapping("/{shopId}")
    public ResponseEntity<GetShopResponse> getById(@PathVariable("shopId") Long id) {
        return ResponseEntity.ok(memberShopService.getById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<GetAllShopResponses> getBySearch(
        @ModelAttribute ShopSearchCondition condition) {
        return ResponseEntity.ok(memberShopService.getBySearch(condition));
    }


}
