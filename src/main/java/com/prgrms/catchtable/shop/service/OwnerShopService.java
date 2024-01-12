package com.prgrms.catchtable.shop.service;

import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.reservation.repository.ReservationTimeRepository;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.dto.ShopMapper;
import com.prgrms.catchtable.shop.dto.request.RegisterShopRequest;
import com.prgrms.catchtable.shop.dto.response.RegisterShopResponse;
import com.prgrms.catchtable.shop.repository.ShopRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerShopService {

    private final ShopRepository shopRepository;
    private final ReservationTimeRepository reservationTimeRepository;

    @Transactional
    public RegisterShopResponse registerShop(RegisterShopRequest registerShopRequest, Owner owner) {

        Shop registerShop = shopRepository.save(ShopMapper.toEntity(registerShopRequest));
        owner.insertShop(registerShop);

        List<ReservationTime> reservationTimeList = registerShopRequest.reservationTimeRequestList()
            .stream()
            .map(time -> ReservationTime.builder()
                .time(time)
                .build())
            .toList();

        for (ReservationTime reservationTime : reservationTimeList) {
            reservationTime.insertShop(registerShop);
            reservationTimeRepository.save(reservationTime);
        }

        return ShopMapper.toRegisterShopResponse(registerShop, reservationTimeList);
    }

}
