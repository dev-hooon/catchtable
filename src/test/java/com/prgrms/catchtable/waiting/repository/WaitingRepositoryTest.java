package com.prgrms.catchtable.waiting.repository;

import static com.prgrms.catchtable.waiting.domain.WaitingStatus.CANCELED;
import static com.prgrms.catchtable.waiting.domain.WaitingStatus.COMPLETED;
import static com.prgrms.catchtable.waiting.domain.WaitingStatus.PROGRESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import com.prgrms.catchtable.member.MemberFixture;
import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.member.repository.MemberRepository;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.fixture.ShopFixture;
import com.prgrms.catchtable.shop.repository.ShopRepository;
import com.prgrms.catchtable.waiting.domain.Waiting;
import com.prgrms.catchtable.waiting.fixture.WaitingFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
class WaitingRepositoryTest {

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
    void findWaitingWithShopAndMember() {
        //given
        Waiting canceledWaiting = WaitingFixture.canceledWaiting(member1, shop, 1);
        Waiting completedWaiting = WaitingFixture.completedWaiting(member1, shop, 2);
        Waiting progressWaiting = WaitingFixture.progressWaiting(member1, shop, 3);

        waitingRepository.saveAll(List.of(canceledWaiting, completedWaiting, progressWaiting));
        //when
        List<Waiting> memberAllWaitings = waitingRepository.findWaitingWithMemberAndShop(member1);
        //then
        assertThat(memberAllWaitings).containsExactly(canceledWaiting, completedWaiting,
            progressWaiting);
    }

    @DisplayName("벌크 연산으로 진행 중인 대기 상태를 취소 상태로 업데이트 할 수 있다.")
    @Test
    void updateWaitingStatus() {
        //given
        Waiting progressWaiting1 = WaitingFixture.progressWaiting(member1, shop, 1);
        Waiting progressWaiting2 = WaitingFixture.progressWaiting(member2, shop, 2);
        Waiting progressWaiting3 = WaitingFixture.progressWaiting(member3, shop, 3);
        Waiting completedWaiting = WaitingFixture.completedWaiting(member3, shop, 4);

        waitingRepository.saveAll(
            List.of(progressWaiting1, progressWaiting2, progressWaiting3, completedWaiting));
        //when
        waitingRepository.updateWaitingStatus(CANCELED, PROGRESS);
        System.out.println("progressWaiting3 = " + progressWaiting3.getStatus());
        Waiting waiting1 = waitingRepository.findById(progressWaiting1.getId()).orElseThrow();
        Waiting waiting2 = waitingRepository.findById(progressWaiting2.getId()).orElseThrow();
        Waiting waiting3 = waitingRepository.findById(progressWaiting3.getId()).orElseThrow();
        Waiting waiting4 = waitingRepository.findById(completedWaiting.getId()).orElseThrow();

        //then
        assertAll(
            () -> assertThat(waiting1.getStatus()).isEqualTo(CANCELED),
            () -> assertThat(waiting2.getStatus()).isEqualTo(CANCELED),
            () -> assertThat(waiting3.getStatus()).isEqualTo(CANCELED),
            () -> assertThat(waiting4.getStatus()).isEqualTo(COMPLETED)
        );
    }
}