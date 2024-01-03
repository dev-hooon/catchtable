package com.prgrms.catchtable.waiting.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
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
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class WaitingControllerTest extends BaseIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private WaitingRepository waitingRepository;
    private List<Waiting> waitings;
    @Autowired
    private ShopRepository shopRepository;
    private Shop shop;
    private Member member1, member2, member3;

    @BeforeEach
    void setUp() {
        shop = ShopFixture.shopWith24();
        shopRepository.save(shop);
        member1 = MemberFixture.member("test1@naver.com");
        member2 = MemberFixture.member("test2@naver.com");
        member3 = MemberFixture.member("test3@naver.com");
        memberRepository.saveAll(List.of(member1, member2, member3));

        Waiting waiting1 = Waiting.builder()
            .member(member1)
            .shop(shop)
            .waitingNumber(1)
            .peopleCount(2)
            .build();
        Waiting waiting2 = Waiting.builder()
            .member(member2)
            .shop(shop)
            .waitingNumber(2)
            .peopleCount(2)
            .build();
        waitings = waitingRepository.saveAll(List.of(waiting1, waiting2));
        waiting2.completeWaiting();
    }


    @DisplayName("웨이팅 생성 API를 호출할 수 있다.")
    @Test
    void createWaiting() throws Exception {
        //given
        CreateWaitingRequest request = CreateWaitingRequest
            .builder()
            .peopleCount(2).build();
        // when, then
        mockMvc.perform(post("/waitings/{shopId}/{memberId}", shop.getId(), member3.getId())
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.shopId").value(shop.getId()))
            .andExpect(jsonPath("$.shopName").value(shop.getName()))
            .andExpect(jsonPath("$.waitingOrder").value(2))
            .andExpect(jsonPath("$.waitingNumber").value(waitings.size() + 1))
            .andExpect(jsonPath("$.peopleCount").value(request.peopleCount()))
            .andDo(MockMvcResultHandlers.print());

    }
}
