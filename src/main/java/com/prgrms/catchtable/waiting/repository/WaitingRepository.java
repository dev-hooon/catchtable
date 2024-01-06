package com.prgrms.catchtable.waiting.repository;

import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.waiting.domain.Waiting;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {

    boolean existsByMember(Member member);

    Optional<Waiting> findByMember(Member member);

    Long countByShopAndCreatedAtBetween(Shop shop, LocalDateTime start, LocalDateTime end);
}
