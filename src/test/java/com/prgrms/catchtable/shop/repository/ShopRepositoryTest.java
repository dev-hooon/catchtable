package com.prgrms.catchtable.shop.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import com.prgrms.catchtable.shop.domain.Category;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.dto.request.ShopSearchCondition;
import com.prgrms.catchtable.shop.fixture.ShopFixture;
import java.util.List;
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
        assertThat(searchList.get(0)).isEqualTo(savedShop);
    }

    @Test
    @DisplayName("카테고리 검색을 통해 Shop을 조회할 수 있다.")
    void findCategorySearchTest() {
        //given
        Shop shop = ShopFixture.shop();
        Shop savedShop = shopRepository.save(shop);
        ShopSearchCondition condition = new ShopSearchCondition(savedShop.getName(), Category.WESTERN_FOOD.getType(), null);

        //when
        List<Shop> searchList = shopRepository.findSearch(condition);

        //then
        assertThat(searchList.get(0)).isEqualTo(savedShop);
    }

    @Test
    @DisplayName("검색 조건 중 하나라도 값이 틀리면 조건이 성립하지 않는다.")
    void findSearchTest() {
        //given
        Shop shop = ShopFixture.shop();
        Shop savedShop = shopRepository.save(shop);
        ShopSearchCondition condition = new ShopSearchCondition(savedShop.getName(), Category.KOREAN_FOOD.getType(), savedShop.getAddress().getCity());

        //when
        List<Shop> searchList = shopRepository.findSearch(condition);

        //then
        assertThat(searchList.size()).isZero();
    }
}