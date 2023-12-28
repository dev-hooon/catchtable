package com.prgrms.catchtable.common.data.shop;

import static com.prgrms.catchtable.shop.domain.Category.JAPANESE_FOOD;

import com.prgrms.catchtable.shop.domain.Address;
import com.prgrms.catchtable.shop.domain.Shop;
import java.math.BigDecimal;

public class ShopData {

    public static Shop getShop() {
        return Shop.builder()
            .name("shopA")
            .rating(BigDecimal.valueOf(5L))
            .category(JAPANESE_FOOD)
            .address(Address.builder().build())
            .capacity(30)
            .build();
    }
}
