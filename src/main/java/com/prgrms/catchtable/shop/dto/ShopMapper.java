package com.prgrms.catchtable.shop.dto;

import com.prgrms.catchtable.shop.domain.Address;
import com.prgrms.catchtable.shop.domain.Category;
import com.prgrms.catchtable.shop.domain.Shop;
import java.math.BigDecimal;

public class ShopMapper {

    public static Shop toEntity(RegistShopRequest registShopRequest){

        return Shop.builder()
            .name(registShopRequest.name())
            .rating(BigDecimal.valueOf(registShopRequest.rating()))
            .category(Category.of(registShopRequest.category()))
            .address(Address.builder()
                .city(registShopRequest.city())
                .district(registShopRequest.district())
                .build())
            .capacity(registShopRequest.capacity())
            .openingTime(registShopRequest.openingTime())
            .closingTime(registShopRequest.closingTime())
            .build();
    }

    public static RegistShopResponse of(Shop shop){
        return RegistShopResponse.builder()
            .name(shop.getName())
            .rating(Integer.parseInt(String.valueOf(shop.getRating())))
            .category(shop.getCategory().getType())
            .city(shop.getAddress().getCity())
            .district(shop.getAddress().getDistrict())
            .capacity(shop.getCapacity())
            .openingTime(shop.getOpeningTime())
            .closingTime(shop.getClosingTime())
            .build();
    }

}
