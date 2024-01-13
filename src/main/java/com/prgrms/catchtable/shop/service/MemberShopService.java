package com.prgrms.catchtable.shop.service;

import static com.prgrms.catchtable.common.exception.ErrorCode.NOT_EXIST_SHOP;

import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.reservation.repository.ReservationTimeRepository;
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
    private final ReservationTimeRepository reservationTimeRepository;

    @Transactional(readOnly = true)
    public GetAllShopResponses getAll() {
        List<Shop> allShop = shopRepository.findAll();
        return ShopMapper.toGetAllShopResponses(allShop);
    }

    @Transactional(readOnly = true)
    public GetShopResponse getById(Long id) {
        //예약시간 조회
        List<ReservationTime> reservationTime = reservationTimeRepository.findByShopId(id);

        //가게와 메뉴 조회
        Shop findShop = shopRepository.findShopById(id)
            .orElseThrow(() -> new NotFoundCustomException(NOT_EXIST_SHOP));

        return ShopMapper.toGetShopResponse(findShop, reservationTime);
    }

    @Transactional(readOnly = true)
    public GetAllShopResponses getBySearch(ShopSearchCondition condition) {
        List<Shop> searchShop = shopRepository.findSearch(condition);
        return ShopMapper.toGetAllShopResponses(searchShop);
    }
}
