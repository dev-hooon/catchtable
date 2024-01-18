package com.prgrms.catchtable.shop.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.shop.fixture.ShopFixture;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ShopTest {

    @DisplayName("가게 영업 시간이 아닐 때는 예외를 발생시킨다.")
    @Test
    void validate_shop_not_running_time() {
        //given
        LocalTime beforeOpeningTime = LocalTime.of(5, 59);
        LocalTime afterClosingTime = LocalTime.of(23, 1);
        Shop shop = ShopFixture.shop();
        //when, then
        assertThrows(
            BadRequestCustomException.class,
            () -> shop.validateIfShopOpened(beforeOpeningTime));
        assertThrows(
            BadRequestCustomException.class,
            () -> shop.validateIfShopOpened(afterClosingTime));
    }

    @DisplayName("가게 영업 시간 내에서는 예외를 발생시키지 않는다.")
    @Test
    void validate_shop_running_time() {
        //given
        LocalTime openingTime = LocalTime.of(6, 0);
        LocalTime closingTime = LocalTime.of(23, 0);
        Shop shop = ShopFixture.shop();
        //when, then
        assertDoesNotThrow(
            () -> shop.validateIfShopOpened(openingTime));
        assertDoesNotThrow(
            () -> shop.validateIfShopOpened(closingTime));
    }
}