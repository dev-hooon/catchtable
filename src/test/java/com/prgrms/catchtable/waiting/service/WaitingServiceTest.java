package com.prgrms.catchtable.waiting.service;

import static com.prgrms.catchtable.waiting.domain.WaitingStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.member.repository.MemberRepository;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.repository.ShopRepository;
import com.prgrms.catchtable.waiting.domain.Waiting;
import com.prgrms.catchtable.waiting.domain.WaitingStatus;
import com.prgrms.catchtable.waiting.dto.CreateWaitingRequest;
import com.prgrms.catchtable.waiting.dto.WaitingResponse;
import com.prgrms.catchtable.waiting.repository.WaitingRepository;
import com.prgrms.catchtable.waiting.repository.waitingline.WaitingLineRepository;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WaitingServiceTest {

    @Mock
    private WaitingRepository waitingRepository;
    @Mock
    private ShopRepository shopRepository;
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private WaitingLineRepository waitingLineRepository;
    @InjectMocks
    private WaitingService waitingService;

    @Test
    void createWaiting() {
        //given
        CreateWaitingRequest request = CreateWaitingRequest.builder()
            .peopleCount(2)
            .build();
        Shop shop = mock(Shop.class);
        Member member = mock(Member.class);
        Waiting waiting = Waiting.builder()
            .member(member)
            .shop(shop)
            .waitingNumber(1)
            .peopleCount(2)
            .build();
        doNothing().when(shop).validateIfShopOpened(any(LocalTime.class));
        given(shopRepository.findById(1L)).willReturn(Optional.of(shop));
        given(shop.getId()).willReturn(1L);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(waitingRepository.existsByMember(member)).willReturn(false);
        given(waitingRepository.save(any(Waiting.class))).willReturn(waiting);
        given(waitingLineRepository.findRank(shop.getId(), waiting.getId())).willReturn(1L);

        //when
        WaitingResponse response = waitingService.createWaiting(1L, 1L, request);
        //then
        assertAll(
            () -> assertThat(response.peopleCount()).isEqualTo(2),
            () -> assertThat(response.rank()).isEqualTo(1L),
            () -> assertThat(response.waitingNumber()).isEqualTo(1)
        );
    }

    @DisplayName("대기 지연을 할 수 있다.")
    @Test
    void postponeWaiting() {
        //given
        Shop shop = mock(Shop.class);
        Member member = mock(Member.class);
        Waiting waiting = mock(Waiting.class);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(waitingRepository.findByMemberWithShop(member)).willReturn(Optional.of(waiting));
        given(waiting.getShop()).willReturn(shop);
        given(waiting.getStatus()).willReturn(PROGRESS);
        given(waitingLineRepository.findRank(anyLong(), anyLong())).willReturn(3L);
        doNothing().when(waiting).validatePostponeRemainingCount();
        doNothing().when(waiting).decreasePostponeRemainingCount();

        //when
        WaitingResponse response = waitingService.postponeWaiting(1L);
        //then
        assertAll(
            assertThat(response.peopleCount())::isNotNull,
            assertThat(response.rank())::isNotNull,
            assertThat(response.waitingNumber())::isNotNull
        );
    }

    @DisplayName("대기 취소를 할 수 있다.")
    @Test
    void cancelWaiting() {
        //given
        Shop shop = mock(Shop.class);
        Member member = mock(Member.class);
        Waiting waiting = mock(Waiting.class);

        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(waitingRepository.findByMemberWithShop(member)).willReturn(Optional.of(waiting));
        given(waiting.getShop()).willReturn(shop);
        given(waiting.getStatus()).willReturn(CANCELED);
        doNothing().when(waiting).changeStatusCanceled();

        //when
        WaitingResponse response = waitingService.cancelWaiting(1L);

        //then
        assertAll(
            assertThat(response.peopleCount())::isNotNull,
            assertThat(response.rank())::isNotNull,
            assertThat(response.waitingNumber())::isNotNull
        );
    }
}