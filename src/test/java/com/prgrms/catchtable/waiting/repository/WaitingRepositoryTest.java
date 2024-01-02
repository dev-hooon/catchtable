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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;


@SpringBootTest
class WaitingRepositoryTest {

    @Autowired
    private WaitingRepository waitingRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ShopRepository shopRepository;
    private Shop shop;
    private Member member1, member2, member3;
    private Waiting yesterdayWaiting, completedWaiting, normalWaiting;
    private final LocalDateTime START_DATE_TIME = LocalDateTime.of(LocalDate.now(),
        LocalTime.of(0, 0, 0));
    private final LocalDateTime END_DATE_TIME = LocalDateTime.of(LocalDate.now(),
        LocalTime.of(23, 59, 59));

    @BeforeEach
    void setUp() {
        member1 = MemberFixture.member("test1");
        member2 = MemberFixture.member("test2");
        member3 = MemberFixture.member("test3");
        memberRepository.saveAll(List.of(member1, member2, member3));

        shop = ShopFixture.shop();
        shopRepository.save(shop);
    }

    @DisplayName("특정 가게의 당일 대기 번호를 조회할 수 있다.")
    @Test
    void countByShopAndCreatedAtBetween() {
        yesterdayWaiting = WaitingFixture.waiting(member1, shop, 1);
        completedWaiting = WaitingFixture.completedWaiting(member2, shop, 2);
        normalWaiting = WaitingFixture.waiting(member3, shop, 3);
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

    @DisplayName("특정 가게의 당일 대기 순서를 조회할 수 있다.")
    @Test
    void countByShopAndStatusAndCreatedAtBetween() {
        yesterdayWaiting = WaitingFixture.waiting(member1, shop, 1);
        completedWaiting = WaitingFixture.completedWaiting(member2, shop, 2);
        normalWaiting = WaitingFixture.waiting(member3, shop, 3);
        waitingRepository.saveAll(List.of(yesterdayWaiting, completedWaiting, normalWaiting));

        ReflectionTestUtils.setField(yesterdayWaiting, "createdAt",
            LocalDateTime.now().minusDays(1));
        waitingRepository.save(yesterdayWaiting);

        //when
        Long count = waitingRepository.countByShopAndStatusAndCreatedAtBetween(shop, PROGRESS,
            START_DATE_TIME, END_DATE_TIME);
        //then
        assertThat(count).isEqualTo(1L); //waiting3
    }
}