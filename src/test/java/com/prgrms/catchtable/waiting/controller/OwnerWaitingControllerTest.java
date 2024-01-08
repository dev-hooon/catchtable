package com.prgrms.catchtable.waiting.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.prgrms.catchtable.common.base.BaseIntegrationTest;
import com.prgrms.catchtable.member.MemberFixture;
import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.member.repository.MemberRepository;
import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.owner.fixture.OwnerFixture;
import com.prgrms.catchtable.owner.repository.OwnerRepository;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.fixture.ShopFixture;
import com.prgrms.catchtable.shop.repository.ShopRepository;
import com.prgrms.catchtable.waiting.domain.Waiting;
import com.prgrms.catchtable.waiting.repository.WaitingRepository;
import com.prgrms.catchtable.waiting.repository.waitingline.WaitingLineRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

class OwnerWaitingControllerTest extends BaseIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private WaitingRepository waitingRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private WaitingLineRepository waitingLineRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private Member member1, member2, member3;
    private Shop shop;
    private Owner owner;
    private Waiting waiting1, waiting2, waiting3;
    private List<Waiting> waitings;


    @BeforeEach
    void setUp() {
        member1 = MemberFixture.member("test1@naver.com");
        member2 = MemberFixture.member("test2@naver.com");
        member3 = MemberFixture.member("test3@naver.com");
        memberRepository.saveAll(List.of(member1, member2, member3));

        shop = ShopFixture.shopWith24();
        shopRepository.save(shop);

        owner = OwnerFixture.getOwner(shop);
        ownerRepository.save(owner);

        waiting1 = Waiting.builder()
            .member(member1)
            .shop(shop)
            .waitingNumber(1)
            .peopleCount(2)
            .build();
        waiting2 = Waiting.builder()
            .member(member2)
            .shop(shop)
            .waitingNumber(2)
            .peopleCount(2)
            .build();

        waiting3 = Waiting.builder()
            .member(member3)
            .shop(shop)
            .waitingNumber(3)
            .peopleCount(2)
            .build();

        waitings = waitingRepository.saveAll(List.of(waiting1, waiting2, waiting3));
        waitingLineRepository.save(shop.getId(), waiting1.getId());
        waitingLineRepository.save(shop.getId(), waiting2.getId());
        waitingLineRepository.save(shop.getId(), waiting3.getId());
    }

    @AfterEach
    void clear() {
        redisTemplate.delete("s" + shop.getId());
        memberRepository.deleteAll();
        shopRepository.deleteAll();
        ownerRepository.deleteAll();
        waitingRepository.deleteAll();
    }

    @DisplayName("웨이팅 조회 API를 호출할 수 있다.")
    @Test
    void getWaiting() throws Exception {
        //when, then
        mockMvc.perform(get("/owner/waitings/{ownerId}", owner.getId())
                .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.shopWaitings", hasSize(3)))
            .andExpect(jsonPath("$.shopWaitings[0].waitingId").value(waiting1.getId()))
            .andExpect(
                jsonPath("$.shopWaitings[0].waitingNumber").value(waiting1.getWaitingNumber()))
            .andExpect(jsonPath("$.shopWaitings[0].rank").value(1L))
            .andExpect(jsonPath("$.shopWaitings[0].peopleCount").value(waiting1.getPeopleCount()))
            .andExpect(jsonPath("$.shopWaitings[1].waitingId").value(waiting2.getId()))
            .andExpect(
                jsonPath("$.shopWaitings[1].waitingNumber").value(waiting2.getWaitingNumber()))
            .andExpect(jsonPath("$.shopWaitings[1].rank").value(2L))
            .andExpect(jsonPath("$.shopWaitings[1].peopleCount").value(waiting2.getPeopleCount()))
        ;
    }

}