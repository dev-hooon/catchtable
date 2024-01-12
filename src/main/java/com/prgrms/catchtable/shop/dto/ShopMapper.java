package com.prgrms.catchtable.shop.dto;

import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.shop.domain.Address;
import com.prgrms.catchtable.shop.domain.Category;
import com.prgrms.catchtable.shop.domain.Menu;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.dto.request.RegisterShopRequest;
import com.prgrms.catchtable.shop.dto.response.GetAllShopResponse;
import com.prgrms.catchtable.shop.dto.response.GetAllShopResponses;
import com.prgrms.catchtable.shop.dto.response.GetShopResponse;
import com.prgrms.catchtable.shop.dto.response.MenuResponse;
import com.prgrms.catchtable.shop.dto.response.RegisterShopResponse;
import com.prgrms.catchtable.shop.dto.response.ReservationTimeResponse;
import java.util.List;

public class ShopMapper {

    public static Shop toEntity(RegisterShopRequest registerShopRequest) {
        List<Menu> menuList = registerShopRequest.menuRequestList()
            .stream()
            .map(menu -> Menu.builder()
                .name(menu.name())
                .price(menu.price())
                .description(menu.description())
                .build())
            .toList();

        Shop registerShop = Shop.builder()
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
        registerShop.updateMenuList(menuList);
        return registerShop;
    }

    public static RegisterShopResponse toRegisterShopResponse(Shop shop,
        List<ReservationTime> reservationTimeList) {

        List<ReservationTimeResponse> timeResponses = reservationTimeList.stream()
            .map(time -> ReservationTimeResponse.builder()
                .id(time.getId())
                .reservationTime(time.getTime())
                .build())
            .toList();

        List<MenuResponse> menuResponses = shop.getMenuList()
            .stream()
            .map(menu -> MenuResponse.builder()
                .id(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .description(menu.getDescription())
                .build()
            )
            .toList();

        return RegisterShopResponse.builder()
            .id(shop.getId())
            .name(shop.getName())
            .rating(shop.getRating())
            .category(shop.getCategory().getType())
            .city(shop.getAddress().getCity())
            .district(shop.getAddress().getDistrict())
            .capacity(shop.getCapacity())
            .openingTime(shop.getOpeningTime())
            .closingTime(shop.getClosingTime())
            .reservationTimeResponseList(timeResponses)
            .menuResponseList(menuResponses)
            .build();
    }

    public static GetAllShopResponses toGetAllShopResponses(List<Shop> shops) {
        return new GetAllShopResponses(shops.stream()
            .map(ShopMapper::toGetAllShopResponse)
            .toList());
    }

    private static GetAllShopResponse toGetAllShopResponse(Shop shop) {
        return GetAllShopResponse.builder()
            .id(shop.getId())
            .name(shop.getName())
            .openingTime(shop.getOpeningTime())
            .closingTime(shop.getClosingTime())
            .build();
    }

    public static GetShopResponse toGetShopResponse(Shop shop, List<ReservationTime> reservationTimeList) {

        List<MenuResponse> menuResponses = shop.getMenuList().stream()
            .map(menu -> MenuResponse.builder()
                .id(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .description(menu.getDescription())
                .build())
            .toList();

        List<ReservationTimeResponse> timeResponses = reservationTimeList.stream()
            .map(time -> ReservationTimeResponse.builder()
                .id(time.getId())
                .reservationTime(time.getTime())
                .build())
            .toList();

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
            .reservationTimeResponseList(timeResponses)
            .menuResponseList(menuResponses)
            .build();
    }

}
