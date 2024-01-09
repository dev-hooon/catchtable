package com.prgrms.catchtable.shop.service;

import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.dto.request.RegistShopRequest;
import com.prgrms.catchtable.shop.dto.response.RegistShopResponse;
import com.prgrms.catchtable.shop.dto.ShopMapper;
import com.prgrms.catchtable.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerShopService {

    private final ShopRepository shopRepository;

    @Transactional
    public RegistShopResponse registShop(RegistShopRequest registShopRequest, Owner owner){

        Shop registShop = shopRepository.save(ShopMapper.toEntity(registShopRequest));
        owner.insertShop(registShop);

        return ShopMapper.toRegistShopResponse(registShop);

    }

}
