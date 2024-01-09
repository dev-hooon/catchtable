package com.prgrms.catchtable.shop.service;

import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.dto.GetAllShopResponse;
import com.prgrms.catchtable.shop.dto.ShopMapper;
import com.prgrms.catchtable.shop.repository.ShopRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberShopService {

    private final ShopRepository shopRepository;

    @Transactional(readOnly = true)
    public GetAllShopResponse getAll() {
        List<Shop> allShop = shopRepository.findAll();
        return ShopMapper.toGetAllShopResponse(allShop);
    }
}
