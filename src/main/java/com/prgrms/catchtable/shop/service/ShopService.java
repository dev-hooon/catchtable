package com.prgrms.catchtable.shop.service;

import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.dto.RegistShopRequest;
import com.prgrms.catchtable.shop.dto.RegistShopResponse;
import com.prgrms.catchtable.shop.dto.ShopMapper;
import com.prgrms.catchtable.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;

    public RegistShopResponse registShop(RegistShopRequest registShopRequest, Owner owner){

        Shop registShop = shopRepository.save(ShopMapper.toEntity(registShopRequest));
        owner.insertShop(registShop);

        return ShopMapper.of(registShop);

    }

}
