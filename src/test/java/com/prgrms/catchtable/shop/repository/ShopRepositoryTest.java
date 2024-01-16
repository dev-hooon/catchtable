package com.prgrms.catchtable.shop.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import com.prgrms.catchtable.shop.domain.Category;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.dto.request.ShopSearchCondition;
import com.prgrms.catchtable.shop.fixture.ShopFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class ShopRepositoryTest {

    @Autowired
    private ShopRepository shopRepository;

    @BeforeEach
    void init() {
        shopRepository.deleteAll();
    }

    @Test
    @DisplayName("이름 검색을 통해 Shop을 조회할 수 있다.")
    void findNameSearchTest() {
        //given
        Shop shop = ShopFixture.shop();
        Shop savedShop = shopRepository.save(shop);
        ShopSearchCondition condition = new ShopSearchCondition(savedShop.getName(), null, null);

        //when
        List<Shop> searchList = shopRepository.findSearch(condition);

        //then
        assertThat(searchList.get(0).getId()).isEqualTo(savedShop.getId());
    }

    @Test
    @DisplayName("카테고리 검색을 통해 Shop을 조회할 수 있다.")
    void findCategorySearchTest() {
        //given
        Shop shop = ShopFixture.shop();
        Shop savedShop = shopRepository.save(shop);
        ShopSearchCondition condition = new ShopSearchCondition(savedShop.getName(),
            Category.WESTERN_FOOD.getType(), null);

        //when
        List<Shop> searchList = shopRepository.findSearch(condition);

        //then
        assertThat(searchList.get(0).getId()).isEqualTo(savedShop.getId());
    }

    @Test
    @DisplayName("검색 조건 중 하나라도 값이 틀리면 조건이 성립하지 않는다.")
    void findSearchTest() {
        //given
        Shop shop = ShopFixture.shop();
        Shop savedShop = shopRepository.save(shop);
        ShopSearchCondition condition = new ShopSearchCondition(savedShop.getName(),
            Category.KOREAN_FOOD.getType(), savedShop.getAddress().getCity());

        //when
        List<Shop> searchList = shopRepository.findSearch(condition);

        //then
        assertThat(searchList.size()).isZero();
    }

    @Test
    @DisplayName("벌크 연산으로 가게 웨이팅 수를 0으로 만들 수 있다.")
    void updateWaitingStatus() {
        //given
        Shop shop1 = ShopFixture.shop();
        shop1.findWaitingNumber(); // waitingCount 증가
        Shop shop2 = ShopFixture.shop();
        shop2.findWaitingNumber();
        shopRepository.saveAll(List.of(shop1, shop2));

        //when
        shopRepository.initWaitingCount();
        Shop savedShop1 = shopRepository.findById(shop1.getId()).orElseThrow();
        Shop savedShop2 = shopRepository.findById(shop2.getId()).orElseThrow();

        //then
        assertAll(
            () -> assertThat(savedShop1.getWaitingCount()).isZero(),
            () -> assertThat(savedShop2.getWaitingCount()).isZero()
        );
    }
}