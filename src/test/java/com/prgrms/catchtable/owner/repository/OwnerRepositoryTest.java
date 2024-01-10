package com.prgrms.catchtable.owner.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.owner.fixture.OwnerFixture;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.fixture.ShopFixture;
import com.prgrms.catchtable.shop.repository.ShopRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class OwnerRepositoryTest {

    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private ShopRepository shopRepository;

    @Test
    @DisplayName("매장을 통해 점주를 찾을 수 있다")
    void findByShop() {
        Shop shop = ShopFixture.shop();
        Shop savedShop = shopRepository.save(shop);

        Owner owner = OwnerFixture.getOwner(savedShop);
        Owner savedOwner = ownerRepository.save(owner);

        Owner findOwner = ownerRepository.findOwnerByShop(savedShop).orElseThrow();

        assertThat(findOwner).isEqualTo(savedOwner);
    }
}