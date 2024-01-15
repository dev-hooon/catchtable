package com.prgrms.catchtable.waiting.service;

import static com.prgrms.catchtable.waiting.domain.WaitingStatus.CANCELED;
import static com.prgrms.catchtable.waiting.domain.WaitingStatus.PROGRESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.owner.repository.OwnerRepository;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.repository.ShopRepository;
import com.prgrms.catchtable.waiting.domain.Waiting;
import com.prgrms.catchtable.waiting.dto.request.CreateWaitingRequest;
import com.prgrms.catchtable.waiting.dto.response.MemberWaitingHistoryListResponse;
import com.prgrms.catchtable.waiting.dto.response.MemberWaitingResponse;
import com.prgrms.catchtable.waiting.repository.WaitingRepository;
import com.prgrms.catchtable.waiting.repository.waitingline.WaitingLineRepository;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class MemberWaitingServiceTest {

    @Mock
    WaitingNotification waitingNotification;

    @Mock
    private OwnerRepository ownerRepository;
    @Mock
    private WaitingRepository waitingRepository;
    @Mock
    private ShopRepository shopRepository;
    @Mock
    private WaitingLineRepository waitingLineRepository;
    @InjectMocks
    private MemberWaitingService memberWaitingService;

    @DisplayName("웨이팅을 생성할 수 있다.")
    @Test
    void createWaiting() {
        //given
        CreateWaitingRequest request = CreateWaitingRequest.builder()
            .peopleCount(2)
            .build();
        Shop shop = mock(Shop.class);
        Member member = mock(Member.class);
        Owner owner = mock(Owner.class);
        Waiting waiting = Waiting.builder()
            .member(member)
            .shop(shop)
            .waitingNumber(1)
            .peopleCount(2)
            .build();
        given(ownerRepository.findOwnerByShop(shop)).willReturn(Optional.of(owner));
        doNothing().when(shop).validateIfShopOpened(any(LocalTime.class));
        given(shopRepository.findById(1L)).willReturn(Optional.of(shop));
        given(shop.getId()).willReturn(1L);
        given(waitingRepository.existsByMemberAndStatus(member, PROGRESS)).willReturn(false);
        given(waitingRepository.save(any(Waiting.class))).willReturn(waiting);
        given(waitingLineRepository.findRank(shop.getId(), waiting.getId())).willReturn(1L);

        //when
        MemberWaitingResponse response = memberWaitingService.createWaiting(1L, member, request);
        //then
        assertAll(
            () -> assertThat(response.peopleCount()).isEqualTo(2),
            () -> assertThat(response.rank()).isEqualTo(1L),
            () -> assertThat(response.waitingNumber()).isEqualTo(1)
        );
    }

    @DisplayName("웨이팅을 연기할 수 있다.")
    @Test
    void postponeWaiting() {
        //given
        Shop shop = mock(Shop.class);
        Member member = mock(Member.class);
        Waiting waiting = mock(Waiting.class);

        given(waitingRepository.findByMemberAndStatusWithShop(member, PROGRESS)).willReturn(
            Optional.of(waiting));
        given(waiting.getShop()).willReturn(shop);
        given(waiting.getStatus()).willReturn(PROGRESS);
        given(waitingLineRepository.findRank(anyLong(), anyLong())).willReturn(3L);
        doNothing().when(waiting).decreasePostponeRemainingCount();

        //when
        MemberWaitingResponse response = memberWaitingService.postponeWaiting(member);
        //then
        assertAll(
            assertThat(response.peopleCount())::isNotNull,
            assertThat(response.rank())::isNotNull,
            assertThat(response.waitingNumber())::isNotNull
        );
    }

    @DisplayName("웨이팅을 취소할 수 있다.")
    @Test
    void cancelWaiting() {
        //given
        Shop shop = mock(Shop.class);
        Member member = mock(Member.class);
        Waiting waiting = mock(Waiting.class);
        Owner owner = mock(Owner.class);

        given(waitingRepository.findByMemberAndStatusWithShop(member, PROGRESS)).willReturn(
            Optional.of(waiting));
        given(ownerRepository.findOwnerByShop(shop)).willReturn(Optional.of(owner));
        given(waiting.getShop()).willReturn(shop);
        given(waiting.getStatus()).willReturn(CANCELED);
        doNothing().when(waiting).changeStatusCanceled();

        //when
        MemberWaitingResponse response = memberWaitingService.cancelWaiting(member);

        //then
        assertAll(
            assertThat(response.peopleCount())::isNotNull,
            assertThat(response.rank())::isNotNull,
            assertThat(response.waitingNumber())::isNotNull
        );
    }

    @DisplayName("회원의 진행 중인 웨이팅를 조회할 수 있다.")
    @Test
    void getWaiting() {
        //given
        Shop shop = mock(Shop.class);
        Member member = mock(Member.class);
        Waiting waiting = mock(Waiting.class);

        given(waitingRepository.findByMemberAndStatusWithShop(member, PROGRESS)).willReturn(
            Optional.of(waiting));
        given(waiting.getShop()).willReturn(shop);
        given(waiting.getStatus()).willReturn(PROGRESS);
        //when
        MemberWaitingResponse response = memberWaitingService.getWaiting(member);

        //then
        assertAll(
            assertThat(response.peopleCount())::isNotNull,
            assertThat(response.rank())::isNotNull,
            assertThat(response.waitingNumber())::isNotNull
        );
    }

    @DisplayName("회원의 웨이팅 목록을 모두 조회할 수 있다.")
    @Test
    void getMemberAllWaiting() {
        //given
        Member member = mock(Member.class);
        Shop shop = mock(Shop.class);
        Waiting waiting = mock(Waiting.class);

        given(waitingRepository.findWaitingWithMemberAndShop(member)).willReturn(
            List.of(waiting));
        given(waiting.getShop()).willReturn(shop);
        given(waiting.getStatus()).willReturn(CANCELED);

        //when
        MemberWaitingHistoryListResponse response = memberWaitingService.getWaitingHistory(
            member);

        //then
        assertAll(
            assertThat(response.memberWaitings().get(0).peopleCount())::isNotNull,
            assertThat(response.memberWaitings().get(0).status())::isNotNull
        );
    }
}