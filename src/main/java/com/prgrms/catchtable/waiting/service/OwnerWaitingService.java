package com.prgrms.catchtable.waiting.service;

import static com.prgrms.catchtable.common.exception.ErrorCode.NOT_EXIST_OWNER;
import static com.prgrms.catchtable.common.exception.ErrorCode.WAITING_DOES_NOT_EXIST;
import static com.prgrms.catchtable.waiting.dto.WaitingMapper.toOwnerWaitingListResponse;
import static com.prgrms.catchtable.waiting.dto.WaitingMapper.toOwnerWaitingResponse;

import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.owner.repository.OwnerRepository;
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
    private final OwnerRepository ownerRepository;

    @Transactional(readOnly = true)
    public OwnerWaitingListResponse getOwnerAllWaiting(Long ownerId) {
        Owner owner = ownerRepository.findById(ownerId)
            .orElseThrow(() -> new BadRequestCustomException(NOT_EXIST_OWNER));
        List<Long> waitingIds = waitingLineRepository.getShopWaitingIdsInOrder(
            owner.getShop().getId());
        List<Waiting> waitings = waitingRepository.findByIds(waitingIds);
        return toOwnerWaitingListResponse(waitings);
    }

    @Transactional
    public OwnerWaitingResponse entryWaiting(Long ownerId) {
        Owner owner = ownerRepository.findById(ownerId)
            .orElseThrow(() -> new BadRequestCustomException(NOT_EXIST_OWNER));
        Long enteredWaitingId = waitingLineRepository.entry(owner.getShop().getId());
        Waiting waiting = waitingRepository.findById(enteredWaitingId)
            .orElseThrow(() -> new NotFoundCustomException(WAITING_DOES_NOT_EXIST));
        waiting.changeStatusCompleted();
        return toOwnerWaitingResponse(waiting, 0L);
    }
}
