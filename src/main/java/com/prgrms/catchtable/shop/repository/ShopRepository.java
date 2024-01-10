package com.prgrms.catchtable.shop.repository;

import com.prgrms.catchtable.shop.domain.Shop;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShopRepository extends JpaRepository<Shop, Long>,  ShopRepositoryCustom{
    @Query("select s from Shop s join fetch s.reservationTimeList where s.id = :id ")
    Optional<Shop> findByIdWithTime(@Param("id") Long id);
}
