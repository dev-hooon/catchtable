package com.prgrms.catchtable.waiting.repository;

import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.waiting.domain.Waiting;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {

    boolean existsByMember(Member member);

    @Query("select w from Waiting w join fetch w.shop where w.member = :member")
    Optional<Waiting> findByMemberWithShop(@Param("member") Member member);

    Long countByShopAndCreatedAtBetween(Shop shop, LocalDateTime start, LocalDateTime end);
}
