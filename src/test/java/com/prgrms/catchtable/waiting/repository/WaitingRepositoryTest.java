package com.prgrms.catchtable.waiting.repository;

import static com.prgrms.catchtable.waiting.domain.WaitingStatus.PROGRESS;
import static org.assertj.core.api.Assertions.assertThat;

import com.prgrms.catchtable.member.MemberFixture;
import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.member.repository.MemberRepository;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.fixture.ShopFixture;
import com.prgrms.catchtable.shop.repository.ShopRepository;
import com.prgrms.catchtable.waiting.domain.Waiting;
import com.prgrms.catchtable.waiting.fixture.WaitingFixture;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
@Transactional
class WaitingRepositoryTest {

    private final LocalDateTime START_DATE_TIME = LocalDateTime.of(LocalDate.now(),
        LocalTime.of(0, 0, 0));
    private final LocalDateTime END_DATE_TIME = LocalDateTime.of(LocalDate.now(),
        LocalTime.of(23, 59, 59));
    @Autowired
    private WaitingRepository waitingRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ShopRepository shopRepository;
    private Shop shop;
    private Member member1, member2, member3;

    @BeforeEach
    void setUp() {
        member1 = MemberFixture.member("test1");
        member2 = MemberFixture.member("test2");
        member3 = MemberFixture.member("test3");
        memberRepository.saveAll(List.of(member1, member2, member3));

        shop = ShopFixture.shop();
        shopRepository.save(shop);
    }

    @AfterEach
    void clear() {
        memberRepository.deleteAll();
        waitingRepository.deleteAll();
        shopRepository.deleteAll();
    }

    @DisplayName("특정 가게의 당일 대기 번호를 조회할 수 있다.")
    @Test
    void countByShopAndCreatedAtBetween() {
        //given
        Waiting yesterdayWaiting = WaitingFixture.progressWaiting(member1, shop, 1);
        Waiting completedWaiting = WaitingFixture.completedWaiting(member2, shop, 2);
        Waiting normalWaiting = WaitingFixture.progressWaiting(member3, shop, 3);
        waitingRepository.saveAll(List.of(yesterdayWaiting, completedWaiting, normalWaiting));

        ReflectionTestUtils.setField(yesterdayWaiting, "createdAt",
            LocalDateTime.now().minusDays(1));
        waitingRepository.save(yesterdayWaiting);

        //when
        Long count = waitingRepository.countByShopAndCreatedAtBetween(shop, START_DATE_TIME,
            END_DATE_TIME);
        //then
        assertThat(count).isEqualTo(2L); //waiting2, waiting3
    }

    @DisplayName("멤버의 아이디 리스트로 waiting 목록을 조회 가능하다.")
    @Test
    void findByIdsWithMember() {
        Waiting waiting1 = WaitingFixture.progressWaiting(member1, shop, 1);
        Waiting waiting2 = WaitingFixture.progressWaiting(member2, shop, 2);
        Waiting waiting3 = WaitingFixture.progressWaiting(member3, shop, 3);
        waitingRepository.saveAll(List.of(waiting1, waiting2, waiting3));
        List<Long> waitingIds = List.of(waiting1.getId(), waiting2.getId(), waiting3.getId());
        //when
        List<Waiting> waitings = waitingRepository.findByIds(waitingIds);
        //then
        assertThat(waitings).containsExactly(waiting1, waiting2, waiting3);
    }

    @DisplayName("멤버의 진행 중인 웨이팅을 조회할 수 있다.")
    @Test
    void findByMemberAndStatusWithShop() {
        //given
        Waiting completedWaiting = WaitingFixture.completedWaiting(member1, shop, 1);
        Waiting progressWaiting = WaitingFixture.progressWaiting(member1, shop, 2);
        waitingRepository.saveAll(List.of(completedWaiting, progressWaiting));
        //when
        Waiting waiting = waitingRepository.findByMemberAndStatusWithShop(
            member1, PROGRESS).orElseThrow();

        //then
        assertThat(waiting.getWaitingNumber()).isEqualTo(2);
    }

    @DisplayName("특정 멤버의 웨이팅 목록을 조회할 수 있다.")
    @Test
    void findWaitingWithMember() {
        //given
        Waiting canceledWaiting = WaitingFixture.canceledWaiting(member1, shop, 1);
        Waiting completedWaiting = WaitingFixture.completedWaiting(member1, shop, 2);
        Waiting progressWaiting = WaitingFixture.progressWaiting(member1, shop, 3);

        waitingRepository.saveAll(List.of(canceledWaiting, completedWaiting, progressWaiting));
        //when
        List<Waiting> memberAllWaitings = waitingRepository.findWaitingWithMember(member1);
        //then
        assertThat(memberAllWaitings).containsExactly(canceledWaiting, completedWaiting,
            progressWaiting);
    }
}