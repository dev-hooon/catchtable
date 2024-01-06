package com.prgrms.catchtable.waiting.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.prgrms.catchtable.common.base.BaseIntegrationTest;
import com.prgrms.catchtable.member.MemberFixture;
import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.member.repository.MemberRepository;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.fixture.ShopFixture;
import com.prgrms.catchtable.shop.repository.ShopRepository;
import com.prgrms.catchtable.waiting.domain.Waiting;
import com.prgrms.catchtable.waiting.dto.CreateWaitingRequest;
import com.prgrms.catchtable.waiting.repository.WaitingRepository;
import com.prgrms.catchtable.waiting.repository.waitingline.WaitingLineRepository;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Transactional
class WaitingControllerTest extends BaseIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private WaitingRepository waitingRepository;

    @Autowired
    private WaitingLineRepository waitingLineRepository;
    @Autowired
    private ShopRepository shopRepository;
    private Member member1, member2, member3;
    private Shop shop;
    private Waiting waiting1, waiting2, waiting3;
    private List<Waiting> waitings;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @BeforeEach
    void setUp() {
        member1 = MemberFixture.member("test1@naver.com");
        member2 = MemberFixture.member("test2@naver.com");
        member3 = MemberFixture.member("test3@naver.com");
        memberRepository.saveAll(List.of(member1, member2, member3));

        shop = ShopFixture.shopWith24();
        shopRepository.save(shop);

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
    }

    @DisplayName("웨이팅 생성 API를 호출할 수 있다.")
    @Test
    void createWaiting() throws Exception {
        //given
        Member member4 = MemberFixture.member("test4@naver.com");
        memberRepository.save(member4);
        CreateWaitingRequest request = CreateWaitingRequest
            .builder()
            .peopleCount(2).build();

        // when, then
        mockMvc.perform(post("/waitings/{shopId}/{memberId}", shop.getId(), member4.getId())
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.shopId").value(shop.getId()))
            .andExpect(jsonPath("$.shopName").value(shop.getName()))
            .andExpect(jsonPath("$.rank").value(4))
            .andExpect(jsonPath("$.waitingNumber").value(waitings.size() + 1))
            .andExpect(jsonPath("$.peopleCount").value(request.peopleCount()))
            .andDo(MockMvcResultHandlers.print());

    }

    @DisplayName("웨이팅 지연 API를 호출할 수 있다.")
    @Test
    void postponeWaiting() throws Exception {
        //when, then
        mockMvc.perform(patch("/waitings/{memberId}", member2.getId())
                .contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.shopId").value(shop.getId()))
            .andExpect(jsonPath("$.shopName").value(shop.getName()))
            .andExpect(jsonPath("$.rank").value(3))
            .andExpect(jsonPath("$.waitingNumber").value(waiting2.getWaitingNumber()))
            .andExpect(jsonPath("$.peopleCount").value(waiting2.getPeopleCount()))
            .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("맨 뒤의 멤버가 웨이팅 지연 API 호출 시 예외를 반환한다.")
    @Test
    void postponeWaiting_fails() throws Exception {
        mockMvc.perform(patch("/waitings/{memberId}", member3.getId())
                .contentType(APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("이미 맨뒤라 웨이팅을 미룰 수 없습니다."))
            .andDo(MockMvcResultHandlers.print());
//        Waiting waiting = waitingRepository.findById(waiting3.getId()).orElse(null);
//        Assertions.assertThat(waiting.getPostponeRemainingCount()).isEqualTo(2);
    }


    @DisplayName("대기 지연 잔여 횟수를 소진 시, 더이상 지연이 불가하므로 예외를 반환한다.")
    @Test
    void postponeWaiting_fails2() throws Exception {
        ReflectionTestUtils.setField(waiting1,"remainingPostponeCount",0);
        mockMvc.perform(patch("/waitings/{memberId}", member1.getId())
                .contentType(APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("이미 두 차례 대기를 미뤘습니다."))
            .andDo(MockMvcResultHandlers.print());
    }
}