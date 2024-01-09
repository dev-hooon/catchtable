package com.prgrms.catchtable.shop.controller;

import com.prgrms.catchtable.shop.dto.GetAllShopResponse;
import com.prgrms.catchtable.shop.dto.GetShopResponse;
import com.prgrms.catchtable.shop.service.MemberShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shops/")
public class MemberShopController {

    private final MemberShopService memberShopService;

    @GetMapping
    public GetAllShopResponse getAll(){
        return memberShopService.getAll();
    }

    @GetMapping("/{shopId}")
    public GetShopResponse getById(@PathVariable("shopId") Long id){
        return memberShopService.getById(id);
    }


}
