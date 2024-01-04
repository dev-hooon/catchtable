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
import com.prgrms.catchtable.waiting.domain.WaitingStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;


@SpringBootTest
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
    private Waiting waiting1;
    private Waiting waiting2;
    private Waiting waiting3;
    private Member member1, member2, member3;
    private Waiting yesterdayWaiting, completedWaiting, normalWaiting;

    @BeforeEach
    void setUp() {
        Member member1 = MemberFixture.member("test1");
        Member member2 = MemberFixture.member("test2");
        Member member3 = MemberFixture.member("test3");

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        shop = ShopFixture.shop();
        shopRepository.save(shop);

        waiting1 = Waiting.builder()
            .member(member1)
            .shop(shop)
            .waitingNumber(1)
            .waitingOrder(1)
            .peopleCount(2)
            .build();

        waiting2 = Waiting.builder()
            .member(member2)
            .shop(shop)
            .waitingNumber(2)
            .waitingOrder(2)
            .peopleCount(2)
            .build();

        waiting3 = Waiting.builder()
            .member(member3)
            .shop(shop)
            .waitingNumber(3)
            .waitingOrder(3)
            .peopleCount(2)
            .build();
        waitingRepository.save(waiting1);
        waitingRepository.save(waiting2);
        waitingRepository.save(waiting3);
    }

    @DisplayName("특정 가게의 당일 대기 인원을 조회할 수 있다.")
    @Test
    void countByShopAndStatusAndCreatedAtBetween() {
        //given
        ReflectionTestUtils.setField(waiting1, "createdAt", LocalDateTime.now().minusDays(1));
        waitingRepository.save(waiting1); //어제자 대기 생성
        ReflectionTestUtils.setField(waiting2, "status", WaitingStatus.COMPLETED);
        waitingRepository.save(waiting2); //입장상태 대기 생성

        //when
        Long count = waitingRepository.countByShopAndStatusAndCreatedAtBetween(shop, PROGRESS,
            START_DATE_TIME, END_DATE_TIME);
        //then
        assertThat(count).isEqualTo(1L);
    }

    @DisplayName("특정 가게의 당일 대기 인원을 조회할 수 있다.")
    @Test
    void countByShopAndCreatedAtBetween() {
        //given
        ReflectionTestUtils.setField(waiting1, "createdAt", LocalDateTime.now().minusDays(1));
        waitingRepository.save(waiting1);
        ReflectionTestUtils.setField(waiting2, "status", WaitingStatus.COMPLETED);
        waitingRepository.save(waiting2);

        //when
        Long count = waitingRepository.countByShopAndCreatedAtBetween(shop, START_DATE_TIME,
            END_DATE_TIME);
        //then
        assertThat(count).isEqualTo(2L);
    }
}