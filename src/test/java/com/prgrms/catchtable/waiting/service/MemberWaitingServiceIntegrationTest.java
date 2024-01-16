package com.prgrms.catchtable.waiting.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.prgrms.catchtable.member.MemberFixture;
import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.member.repository.MemberRepository;
import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.owner.fixture.OwnerFixture;
import com.prgrms.catchtable.owner.repository.OwnerRepository;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.fixture.ShopFixture;
import com.prgrms.catchtable.shop.repository.ShopRepository;
import com.prgrms.catchtable.waiting.dto.request.CreateWaitingRequest;
import com.prgrms.catchtable.waiting.fixture.WaitingFixture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MemberWaitingServiceIntegrationTest {

    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberWaitingService memberWaitingService;

    private Shop shop;
    private CreateWaitingRequest request;

    @BeforeEach
    void setUp() {
        request = WaitingFixture.createWaitingRequest();

        shop = ShopFixture.shopWith24();
        shopRepository.save(shop);

        Owner owner = OwnerFixture.getOwner(shop);
        ownerRepository.save(owner);
    }

    @DisplayName("동시에 50개 요청이 들어와도 각각 다른 대기번호를 부여한다.")
    @Test
    void createWaitingNumberConcurrency() throws InterruptedException {
        int threadCount = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(
            threadCount); // 다른 thread에서 수행 중인 작업이 완료될 때까지 대기할 수 있도록 돕는 클래스

        for (int i = 0; i < threadCount; i++) {
            Member member = MemberFixture.member(String.format("hyun%d@gmail.com", i)); // validateMemberWaitingExists 오류 안 나도록 (한 기기 당 한 회원 웨이팅 생성)
            memberRepository.save(member);
            executorService.submit(() -> {
                try {
                    memberWaitingService.createWaiting(shop.getId(), member, request);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); //다른 스레드에서 수행중인 작업이 완료될 때까지 대기

        int waitingCount = shopRepository.findById(1L).orElseThrow().getWaitingCount();

        assertEquals(50, waitingCount);
    }
}