package com.prgrms.catchtable.shop.fixture;

import com.prgrms.catchtable.shop.domain.Address;
import com.prgrms.catchtable.shop.domain.Category;
import com.prgrms.catchtable.shop.domain.Shop;
import java.math.BigDecimal;
import java.time.LocalTime;

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
}
