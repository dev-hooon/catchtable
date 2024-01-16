package com.prgrms.catchtable.shop.repository;

import com.prgrms.catchtable.shop.domain.Shop;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ShopRepository extends JpaRepository<Shop, Long>, ShopRepositoryCustom {

    @EntityGraph(attributePaths = {"menuList"})
    Optional<Shop> findShopById(Long id);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update Shop s set s.waitingCount = 0")
    void initWaitingCount();
}
