package com.prgrms.catchtable.shop.repository;

import com.prgrms.catchtable.shop.domain.Shop;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long>, ShopRepositoryCustom {

    @EntityGraph(attributePaths = "reservationTimeList")
    Optional<Shop> findShopById(Long id);
}
