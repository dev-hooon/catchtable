package com.prgrms.catchtable.shop.dto;

import com.prgrms.catchtable.shop.domain.Address;
import com.prgrms.catchtable.shop.domain.Category;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.dto.request.RegistShopRequest;
import com.prgrms.catchtable.shop.dto.response.GetAllShopResponse;
import com.prgrms.catchtable.shop.dto.response.GetShopResponse;
import com.prgrms.catchtable.shop.dto.response.RegistShopResponse;
import java.math.BigDecimal;
import java.util.List;

public class ShopMapper {

    public static Shop toEntity(RegistShopRequest registShopRequest){

        return Shop.builder()
            .name(registShopRequest.name())
            .rating(registShopRequest.rating())
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

    public static RegistShopResponse toRegistShopResponse(Shop shop){
        return RegistShopResponse.builder()
            .name(shop.getName())
            .rating(shop.getRating())
            .category(shop.getCategory().getType())
            .city(shop.getAddress().getCity())
            .district(shop.getAddress().getDistrict())
            .capacity(shop.getCapacity())
            .openingTime(shop.getOpeningTime())
            .closingTime(shop.getClosingTime())
            .build();
    }

    public static GetAllShopResponse toGetAllShopResponse(List<Shop> shops){
        return new GetAllShopResponse(shops.stream()
            .map(ShopMapper::toGetShopResponse)
            .toList());
    }

    public static GetShopResponse toGetShopResponse(Shop shop){
        return GetShopResponse.builder()
            .id(shop.getId())
            .name(shop.getName())
            .rating(shop.getRating())
            .category(shop.getCategory().getType())
            .city(shop.getAddress().getCity())
            .district(shop.getAddress().getDistrict())
            .capacity(shop.getCapacity())
            .openingTime(shop.getOpeningTime())
            .closingTime(shop.getClosingTime())
            .build();
    }

}
