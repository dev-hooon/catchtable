package com.prgrms.catchtable.waiting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.owner.repository.OwnerRepository;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.waiting.domain.Waiting;
import com.prgrms.catchtable.waiting.dto.response.OwnerWaitingListResponse;
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
        List<Long> waitingIds = List.of(1L);
        Owner owner = mock(Owner.class);
        Shop shop = mock(Shop.class);
        Waiting waiting = mock(Waiting.class);

        given(ownerRepository.findById(1L)).willReturn(Optional.of(owner));
        given(owner.getShop()).willReturn(shop);
        given(shop.getId()).willReturn(1L);
        given(waitingLineRepository.getShopWaitingIdsInOrder(any(Long.class))).willReturn(
            waitingIds);
        given(waitingRepository.findByIds(waitingIds)).willReturn(List.of(waiting));

        //when
        OwnerWaitingListResponse response = ownerWaitingService.getOwnerAllWaiting(1L);
        //then
        assertThat(response).isNotNull();
    }
}