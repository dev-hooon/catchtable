package com.prgrms.catchtable.waiting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.owner.repository.OwnerRepository;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.waiting.domain.Waiting;
import com.prgrms.catchtable.waiting.dto.response.OwnerWaitingListResponse;
import com.prgrms.catchtable.waiting.dto.response.OwnerWaitingResponse;
import com.prgrms.catchtable.waiting.fixture.WaitingFixture;
import com.prgrms.catchtable.waiting.repository.WaitingRepository;
import com.prgrms.catchtable.waiting.repository.waitingline.WaitingLineRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OwnerWaitingServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private WaitingRepository waitingRepository;

    @Mock
    private WaitingLineRepository waitingLineRepository;

    @InjectMocks
    private OwnerWaitingService ownerWaitingService;

    @DisplayName("owner의 waiting 목록을 모두 가져온다.")
    @Test
    void getOwnerAllWaiting() {
        //given
        List<Long> waitingIds = List.of(1L, 2L);
        Member member1 = mock(Member.class);
        Member member2 = mock(Member.class);
        Owner owner = mock(Owner.class);
        Shop shop = mock(Shop.class);
        Waiting waiting1 = WaitingFixture.progressWaiting(member1, shop, 1);
        Waiting waiting2 = WaitingFixture.progressWaiting(member2, shop, 2);

        given(ownerRepository.findById(1L)).willReturn(Optional.of(owner));
        given(owner.getShop()).willReturn(shop);
        given(shop.getId()).willReturn(1L);
        given(waitingLineRepository.getShopWaitingIdsInOrder(any(Long.class))).willReturn(
            waitingIds);
        given(waitingRepository.findByIds(waitingIds)).willReturn(List.of(waiting1, waiting2));

        //when
        OwnerWaitingListResponse response = ownerWaitingService.getOwnerAllWaiting(1L);

        //then
        assertThat(response.shopWaitings()).hasSize(2);

        assertThat(response.shopWaitings().get(0).waitingId()).isEqualTo(waiting1.getId());
        assertThat(response.shopWaitings().get(0).waitingNumber()).isEqualTo(
            waiting1.getWaitingNumber());

        assertThat(response.shopWaitings().get(1).waitingId()).isEqualTo(waiting2.getId());
        assertThat(response.shopWaitings().get(1).waitingNumber()).isEqualTo(
            waiting2.getWaitingNumber());
    }

    @DisplayName("웨이팅 손님을 입장시킬 수 있다.")
    @Test
    void entryWaiting() {
        //given
        Member member = mock(Member.class);
        Owner owner = mock(Owner.class);
        Shop shop = mock(Shop.class);
        Waiting waiting = WaitingFixture.progressWaiting(member, shop, 1);

        given(ownerRepository.findById(1L)).willReturn(Optional.of(owner));
        given(owner.getShop()).willReturn(shop);
        given(waitingLineRepository.entry(any(Long.class))).willReturn(1L);
        given(waitingRepository.findById(1L)).willReturn(Optional.of(waiting));
        //when
        OwnerWaitingResponse response = ownerWaitingService.entryWaiting(1L);
        //then
        assertThat(response.waitingId()).isEqualTo(waiting.getId());
        assertThat(response.peopleCount()).isEqualTo(2);
        assertThat(response.rank()).isZero();
        assertThat(response.waitingNumber()).isEqualTo(waiting.getWaitingNumber());

    }
}