package com.prgrms.catchtable.waiting.service;

import static com.prgrms.catchtable.common.exception.ErrorCode.WAITING_DOES_NOT_EXIST;
import static com.prgrms.catchtable.waiting.dto.WaitingMapper.toOwnerWaitingListResponse;
import static com.prgrms.catchtable.waiting.dto.WaitingMapper.toOwnerWaitingResponse;

import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.waiting.domain.Waiting;
import com.prgrms.catchtable.waiting.dto.response.OwnerWaitingListResponse;
import com.prgrms.catchtable.waiting.dto.response.OwnerWaitingResponse;
import com.prgrms.catchtable.waiting.repository.WaitingRepository;
import com.prgrms.catchtable.waiting.repository.waitingline.WaitingLineRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OwnerWaitingService {

    private final WaitingRepository waitingRepository;
    private final WaitingLineRepository waitingLineRepository;
    private final WaitingNotification waitingNotification;

    @Transactional(readOnly = true)
    public OwnerWaitingListResponse getShopAllWaiting(Owner owner) {
        List<Long> waitingIds = waitingLineRepository.getShopWaitingIdsInOrder(
            owner.getShop().getId());
        List<Waiting> waitings = waitingRepository.findByIds(waitingIds);
        return toOwnerWaitingListResponse(waitings);
    }

    @Transactional
    public OwnerWaitingResponse entryWaiting(Owner owner) {
        Long shopId = owner.getShop().getId();
        Long enteredWaitingId = waitingLineRepository.entry(shopId);
        Waiting waiting = getWaitingEntity(enteredWaitingId);
        waiting.changeStatusCompleted();

        waitingNotification.sendMessageAsCompleted(waiting.getMember());
        waitingNotification.sendEntryMessageToOthers(shopId, 1L);

        return toOwnerWaitingResponse(waiting, 0L);
    }

    private Waiting getWaitingEntity(Long waitingId) {
        return waitingRepository.findById(waitingId)
            .orElseThrow(() -> new NotFoundCustomException(WAITING_DOES_NOT_EXIST));
    }
}
