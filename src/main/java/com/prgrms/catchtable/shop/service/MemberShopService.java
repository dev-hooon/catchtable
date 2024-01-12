package com.prgrms.catchtable.shop.service;

import static com.prgrms.catchtable.common.exception.ErrorCode.NOT_EXIST_SHOP;

import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.dto.ShopMapper;
import com.prgrms.catchtable.shop.dto.request.ShopSearchCondition;
import com.prgrms.catchtable.shop.dto.response.GetAllShopResponses;
import com.prgrms.catchtable.shop.dto.response.GetShopResponse;
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
    public GetAllShopResponses getAll() {
        List<Shop> allShop = shopRepository.findAll();
        return ShopMapper.toGetAllShopResponses(allShop);
    }

    @Transactional(readOnly = true)
    public GetShopResponse getById(Long id) {
        Shop findShop = shopRepository.findShopById(id)
            .orElseThrow(() -> new NotFoundCustomException(NOT_EXIST_SHOP));
        return ShopMapper.toGetShopResponse(findShop);
    }

    @Transactional(readOnly = true)
    public GetAllShopResponses getBySearch(ShopSearchCondition condition) {
        List<Shop> searchShop = shopRepository.findSearch(condition);
        return ShopMapper.toGetAllShopResponses(searchShop);
    }
}
