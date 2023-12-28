package com.prgrms.catchtable.waiting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.waiting.domain.Waiting;
import com.prgrms.catchtable.waiting.dto.CreateWaitingRequest;
import com.prgrms.catchtable.waiting.dto.CreateWaitingResponse;
import com.prgrms.catchtable.waiting.facade.WaitingFacade;
import com.prgrms.catchtable.waiting.repository.WaitingRepository;
import java.time.LocalTime;
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
    private WaitingFacade waitingFacade;
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
            .waitingOrder(1)
            .peopleCount(2)
            .build();
        doNothing().when(shop).validateIfShopOpened(any(LocalTime.class));
        given(waitingFacade.getShopEntity(1L)).willReturn(shop);
        given(waitingFacade.getMemberEntity(1L)).willReturn(member);
        given(waitingRepository.existsByMember(member)).willReturn(false);
        given(waitingRepository.save(any(Waiting.class))).willReturn(waiting);

        //when
        CreateWaitingResponse response = waitingService.createWaiting(1L, request);
        //then
        assertAll(
            () -> assertThat(response.peopleCount()).isEqualTo(2),
            () -> assertThat(response.waitingOrder()).isEqualTo(1),
            () -> assertThat(response.waitingNumber()).isEqualTo(1)
        );
    }
}