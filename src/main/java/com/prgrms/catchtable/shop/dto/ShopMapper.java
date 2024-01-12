package com.prgrms.catchtable.shop.dto;

import com.prgrms.catchtable.shop.domain.Address;
import com.prgrms.catchtable.shop.domain.Category;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.dto.request.RegisterShopRequest;
import com.prgrms.catchtable.shop.dto.response.GetAllShopResponse;
import com.prgrms.catchtable.shop.dto.response.GetShopResponse;
import com.prgrms.catchtable.shop.dto.response.RegisterShopResponse;
import java.util.List;

public class ShopMapper {

    public static Shop toEntity(RegisterShopRequest registerShopRequest) {

        return Shop.builder()
            .name(registerShopRequest.name())
            .rating(registerShopRequest.rating())
            .category(Category.of(registerShopRequest.category()))
            .address(Address.builder()
                .city(registerShopRequest.city())
                .district(registerShopRequest.district())
                .build())
            .capacity(registerShopRequest.capacity())
            .openingTime(registerShopRequest.openingTime())
            .closingTime(registerShopRequest.closingTime())
            .build();
    }

    public static RegisterShopResponse toRegisterShopResponse(Shop shop) {
        return RegisterShopResponse.builder()
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

    public static GetAllShopResponse toGetAllShopResponse(List<Shop> shops) {
        return new GetAllShopResponse(shops.stream()
            .map(ShopMapper::toGetShopResponse)
            .toList());
    }

    public static GetShopResponse toGetShopResponse(Shop shop) {
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
