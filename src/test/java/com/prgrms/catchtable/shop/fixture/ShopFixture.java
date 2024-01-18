package com.prgrms.catchtable.shop.fixture;

import com.prgrms.catchtable.shop.domain.Address;
import com.prgrms.catchtable.shop.domain.Category;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.dto.request.RegisterMenuRequest;
import com.prgrms.catchtable.shop.dto.request.RegisterShopRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ShopFixture {

    public static Shop shop() {
        return Shop.builder()
            .name("testShop")
            .rating(BigDecimal.valueOf(3.5))
            .category(Category.WESTERN_FOOD)
            .address(new Address("서울시", "중구"))
            .capacity(30)
            .openingTime(LocalTime.of(6, 0))
            .closingTime(LocalTime.of(23, 0))
            .build();
    }

    public static Shop shopWith24() {
        return Shop.builder()
            .name("testShop")
            .rating(BigDecimal.valueOf(3.5))
            .category(Category.WESTERN_FOOD)
            .address(new Address("서울시", "중구"))
            .capacity(30)
            .openingTime(LocalTime.of(0, 0))
            .closingTime(LocalTime.of(23, 59, 59))
            .build();
    }

    public static RegisterShopRequest getRequestDto(Shop shop) {
        List<LocalDateTime> reservationTimeList = List.of(
            LocalDateTime.of(2024, 2, 2, 12, 0),
            LocalDateTime.of(2024, 2, 2, 18, 0),
            LocalDateTime.of(2024, 2, 2, 19, 0));

        List<RegisterMenuRequest> menuRequestList = List.of(
            new RegisterMenuRequest("돈까스", 11000, "경양식 돈까스"),
            new RegisterMenuRequest("냉모밀&알밥세트", 13000, "세트 묶음")
        );

        return RegisterShopRequest.builder()
            .name(shop.getName())
            .rating(shop.getRating())
            .category(shop.getCategory().getType())
            .city(shop.getAddress().getCity())
            .district(shop.getAddress().getDistrict())
            .capacity(shop.getCapacity())
            .openingTime(shop.getOpeningTime())
            .closingTime(shop.getClosingTime())
            .reservationTimeRequestList(reservationTimeList)
            .menuRequestList(menuRequestList)
            .build();
    }
}
