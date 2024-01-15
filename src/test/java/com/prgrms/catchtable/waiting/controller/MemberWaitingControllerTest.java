package com.prgrms.catchtable.waiting.controller;

import static com.prgrms.catchtable.common.Role.MEMBER;
import static com.prgrms.catchtable.common.exception.ErrorCode.ALREADY_PROGRESS_WAITING_EXISTS;
import static com.prgrms.catchtable.waiting.domain.WaitingStatus.CANCELED;
import static com.prgrms.catchtable.waiting.domain.WaitingStatus.COMPLETED;
import static com.prgrms.catchtable.waiting.domain.WaitingStatus.PROGRESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.prgrms.catchtable.common.base.BaseIntegrationTest;
import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import com.prgrms.catchtable.jwt.token.Token;
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
import com.prgrms.catchtable.waiting.dto.request.CreateWaitingRequest;
import com.prgrms.catchtable.waiting.fixture.WaitingFixture;
import com.prgrms.catchtable.waiting.repository.WaitingRepository;
import com.prgrms.catchtable.waiting.repository.waitingline.WaitingLineRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@Slf4j
class MemberWaitingControllerTest extends BaseIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private WaitingRepository waitingRepository;
    @Autowired
    private WaitingLineRepository waitingLineRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private StringRedisTemplate redisTemplate;
    private Member member1, member2, member3;
    private Shop shop;
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

        Owner owner = OwnerFixture.getOwner("owner@naver.com", "owner");
        owner.insertShop(shop);
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
        waitingRepository.deleteAll();
    }

    @DisplayName("웨이팅 생성 API를 호출할 수 있다.")
    @Test
    void createWaiting() throws Exception {
        //given
        CreateWaitingRequest request = WaitingFixture.createWaitingRequest();
        Member member4 = MemberFixture.member("test4@naver.com");
        memberRepository.save(member4);

        // when, then
        mockMvc.perform(post("/waitings/{shopId}", shop.getId())
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request))
                .headers(getHttpHeaders(member4)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.shopId").value(shop.getId()))
            .andExpect(jsonPath("$.shopName").value(shop.getName()))
            .andExpect(jsonPath("$.rank").value(4))
            .andExpect(jsonPath("$.waitingNumber").value(waitings.size() + 1))
            .andExpect(jsonPath("$.peopleCount").value(request.peopleCount()))
            .andExpect(jsonPath("$.status").value(PROGRESS.getDescription()))
            .andDo(MockMvcResultHandlers.print());

    }

    @DisplayName("회원이 이미 취소된 웨이팅이 있어도, 해당 회원은 웨이팅을 생성할 수 없다.")
    @Test
    void createWaitingSuccess() throws Exception {
        //given
        CreateWaitingRequest request = WaitingFixture.createWaitingRequest();

        waiting1.changeStatusCanceled();
        waitingRepository.save(waiting1);

        // when, then
        mockMvc.perform(post("/waitings/{shopId}", shop.getId())
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request))
                .headers(getHttpHeaders(member1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.shopId").value(shop.getId()))
            .andExpect(jsonPath("$.shopName").value(shop.getName()))
            .andExpect(jsonPath("$.rank").value(4))
            .andExpect(jsonPath("$.waitingNumber").value(waitings.size() + 1))
            .andExpect(jsonPath("$.peopleCount").value(request.peopleCount()))
            .andExpect(jsonPath("$.status").value(PROGRESS.getDescription()))
            .andDo(MockMvcResultHandlers.print());

    }

    @DisplayName("회원이 이미 진행 중인 웨이팅이 있을 경우, 해당 회원은 웨이팅을 생성할 수 없다.")
    @Test
    void createWaitingFails() throws Exception {
        //given
        CreateWaitingRequest request = WaitingFixture.createWaitingRequest();

        // when, then
        mockMvc.perform(post("/waitings/{shopId}", shop.getId())
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request))
                .headers(getHttpHeaders(member1)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(ALREADY_PROGRESS_WAITING_EXISTS.getMessage()))
            .andDo(MockMvcResultHandlers.print());

    }

    @DisplayName("웨이팅 지연 API를 호출할 수 있다.")
    @Test
    void postponeWaiting() throws Exception {
        //when, then
        mockMvc.perform(patch("/waitings")
                .contentType(APPLICATION_JSON)
                .headers(getHttpHeaders(member2)))
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
        mockMvc.perform(patch("/waitings")
                .contentType(APPLICATION_JSON)
                .headers(getHttpHeaders(member3)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("이미 맨뒤라 웨이팅을 미룰 수 없습니다."))
            .andDo(MockMvcResultHandlers.print());
        assertThat(waiting3.getRemainingPostponeCount()).isEqualTo(2);
    }


    @DisplayName("대기 지연 잔여 횟수를 소진 시, 더이상 지연이 불가하므로 예외를 반환한다.")
    @Test
    void postponeWaiting_fails2() throws Exception {
        ReflectionTestUtils.setField(waiting2, "remainingPostponeCount", 0);
        waitingRepository.save(waiting2);
        mockMvc.perform(patch("/waitings")
                .contentType(APPLICATION_JSON)
                .headers(getHttpHeaders(member2)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("이미 두 차례 대기를 미뤘습니다."))
            .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("웨이팅 취소 API를 호출할 수 있다.")
    @Test
    void cancelWaiting() throws Exception {
        //when, then
        mockMvc.perform(delete("/waitings")
                .contentType(APPLICATION_JSON)
                .headers(getHttpHeaders(member1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.shopId").value(shop.getId()))
            .andExpect(jsonPath("$.shopName").value(shop.getName()))
            .andExpect(jsonPath("$.rank").value(-1L))
            .andExpect(jsonPath("$.waitingNumber").value(waiting1.getWaitingNumber()))
            .andExpect(jsonPath("$.peopleCount").value(waiting1.getPeopleCount()))
            .andExpect(jsonPath("$.status").value(CANCELED.getDescription()))
            .andDo(MockMvcResultHandlers.print());
        waitingLineRepository.printWaitingLine(shop.getId());
        assertThat(waitingLineRepository.findRank(shop.getId(), waiting2.getId())).isEqualTo(1L);
        assertThat(waitingLineRepository.findRank(shop.getId(), waiting3.getId())).isEqualTo(2L);
        assertThrows(NotFoundCustomException.class,
            () -> waitingLineRepository.findRank(shop.getId(), waiting1.getId()));
    }

    @DisplayName("진행 중인 웨이팅 조회 API를 호출할 수 있다.")
    @Test
    void getWaiting() throws Exception {
        //when, then
        mockMvc.perform(get("/waitings")
                .contentType(APPLICATION_JSON)
                .headers(getHttpHeaders(member2)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.shopId").value(shop.getId()))
            .andExpect(jsonPath("$.shopName").value(shop.getName()))
            .andExpect(jsonPath("$.rank").value(2L))
            .andExpect(jsonPath("$.waitingNumber").value(waiting2.getWaitingNumber()))
            .andExpect(jsonPath("$.peopleCount").value(waiting2.getPeopleCount()))
            .andExpect(jsonPath("$.status").value(PROGRESS.getDescription()))
            .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("회원의 웨이팅 이력 조회 API를 호출할 수 있다.")
    @Test
    void getMemberWaitingHistory() throws Exception {
        //when, then
        Waiting canceledWaiting = WaitingFixture.canceledWaiting(member1, shop, 23);
        Waiting completedWaiting = WaitingFixture.completedWaiting(member1, shop, 233);
        waitingRepository.saveAll(List.of(canceledWaiting, completedWaiting));
        mockMvc.perform(get("/waitings/history")
                .contentType(APPLICATION_JSON)
                .headers(getHttpHeaders(member1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.memberWaitings", hasSize(3)))
            .andExpect(jsonPath("$.memberWaitings[0].waitingId").value(waiting1.getId()))
            .andExpect(jsonPath("$.memberWaitings[0].status").value(PROGRESS.getDescription()))
            .andExpect(jsonPath("$.memberWaitings[1].waitingId").value(canceledWaiting.getId()))
            .andExpect(jsonPath("$.memberWaitings[1].status").value(CANCELED.getDescription()))
            .andExpect(jsonPath("$.memberWaitings[2].waitingId").value(completedWaiting.getId()))
            .andExpect(jsonPath("$.memberWaitings[2].status").value(COMPLETED.getDescription()))
            .andDo(MockMvcResultHandlers.print());
    }

    private HttpHeaders getHttpHeaders(Member member) {
        Token token = jwtTokenProvider.createToken(member.getEmail(), MEMBER);
        httpHeaders.add("AccessToken", token.getAccessToken());
        httpHeaders.add("RefreshToken", token.getRefreshToken());
        return httpHeaders;
    }
}