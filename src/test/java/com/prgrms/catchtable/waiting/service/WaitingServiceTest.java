package com.prgrms.catchtable.waiting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.member.repository.MemberRepository;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.repository.ShopRepository;
import com.prgrms.catchtable.waiting.domain.Waiting;
import com.prgrms.catchtable.waiting.dto.CreateWaitingRequest;
import com.prgrms.catchtable.waiting.dto.WaitingResponse;
import com.prgrms.catchtable.waiting.repository.WaitingRepository;
import com.prgrms.catchtable.waiting.repository.waitingline.WaitingLineRepository;
import java.time.LocalTime;
import java.util.Optional;
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
}