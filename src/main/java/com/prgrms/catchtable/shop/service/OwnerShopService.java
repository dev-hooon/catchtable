package com.prgrms.catchtable.shop.service;

import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.owner.repository.OwnerRepository;
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
    private final OwnerRepository ownerRepository;
    private final ReservationTimeRepository reservationTimeRepository;

    @Transactional
    public RegisterShopResponse registerShop(RegisterShopRequest registerShopRequest, Owner owner) {

        //가게 저장
        Shop registerShop = shopRepository.save(ShopMapper.toEntity(registerShopRequest));
        //점주와 가게 관계 매핑
        owner.insertShop(registerShop);
        ownerRepository.save(owner);

        //예약 시간 엔티티 생성
        List<ReservationTime> reservationTimeList = registerShopRequest.reservationTimeRequestList()
            .stream()
            .map(time -> ReservationTime.builder()
                .time(time)
                .build())
            .toList();

        //예약 시간과 가게 관계 매핑
        for (ReservationTime reservationTime : reservationTimeList) {
            reservationTime.insertShop(registerShop);
            reservationTimeRepository.save(reservationTime);
        }

        return ShopMapper.toRegisterShopResponse(registerShop, reservationTimeList);
    }

}
