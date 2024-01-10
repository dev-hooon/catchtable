package com.prgrms.catchtable.waiting.repository;

import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.waiting.domain.Waiting;
import com.prgrms.catchtable.waiting.domain.WaitingStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {

    boolean existsByMember(Member member);

    Long countByShopAndCreatedAtBetween(Shop shop, LocalDateTime start, LocalDateTime end);

    @Query("select w from Waiting w join fetch w.shop "
        + "where w.member = :member and w.status = :status")
    Optional<Waiting> findByMemberAndStatusWithShop(@Param("member") Member member,
        @Param("status") WaitingStatus status);

    @Query("select w from Waiting w where w.id in :ids")
    List<Waiting> findByIds(@Param("ids") List<Long> ids);

    @Query("select w from Waiting w "
        + "join fetch w.shop "
        + "join fetch w.member where w.member = :member")
    List<Waiting> findWaitingWithMember(@Param("member") Member member);
}
