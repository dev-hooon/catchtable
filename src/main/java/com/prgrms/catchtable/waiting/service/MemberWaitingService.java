package com.prgrms.catchtable.waiting.service;

import static com.prgrms.catchtable.common.exception.ErrorCode.ALREADY_PROGRESS_WAITING_EXISTS;
import static com.prgrms.catchtable.common.exception.ErrorCode.NOT_EXIST_OWNER;
import static com.prgrms.catchtable.common.exception.ErrorCode.NOT_EXIST_PROGRESS_WAITING;
import static com.prgrms.catchtable.common.exception.ErrorCode.NOT_EXIST_SHOP;
import static com.prgrms.catchtable.waiting.domain.WaitingStatus.PROGRESS;
import static com.prgrms.catchtable.waiting.dto.WaitingMapper.toMemberWaitingListResponse;
import static com.prgrms.catchtable.waiting.dto.WaitingMapper.toMemberWaitingResponse;
import static com.prgrms.catchtable.waiting.dto.WaitingMapper.toWaiting;

import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberWaitingService {

    private final LocalDateTime START_DATE_TIME = LocalDateTime.of(LocalDate.now(),
        LocalTime.of(0, 0, 0));
    private final LocalDateTime END_DATE_TIME = LocalDateTime.of(LocalDate.now(),
        LocalTime.of(23, 59, 59));
    private final WaitingRepository waitingRepository;
    private final ShopRepository shopRepository;
    private final WaitingLineRepository waitingLineRepository;
    private final OwnerRepository ownerRepository;
    private final WaitingNotification notification;

    @Transactional
    public MemberWaitingResponse createWaiting(Long shopId, Member member,
        CreateWaitingRequest request) {
        Shop shop = getShopEntity(shopId); // 연관 엔티티 조회
        Owner owner = getOwnerEntity(shop);

        validateIfMemberWaitingExists(member); // 기존 진행 중인 waiting이 있는지 검증

        int waitingNumber = (waitingRepository.countByShopAndCreatedAtBetween(shop,
            START_DATE_TIME, END_DATE_TIME)).intValue() + 1; // 대기 번호 생성

        Waiting waiting = toWaiting(request, waitingNumber, member, shop); //waiting 생성 후 저장
        Waiting savedWaiting = waitingRepository.save(waiting);

        Long rank = waitingLineRepository.save(shopId, waiting.getId());// 대기열 저장

        notification.sendMessageAsCreated(member, owner, rank);

        return toMemberWaitingResponse(savedWaiting, rank);
    }

    @Transactional
    public MemberWaitingResponse postponeWaiting(Member member) {
        Waiting waiting = getWaitingEntityInProgress(member);
        Long shopId = waiting.getShop().getId();
        Long previousRank = waitingLineRepository.findRank(shopId,
            waiting.getId()); // 미루기 전 rank 저장

        waiting.decreasePostponeRemainingCount();
        Long rank = waitingLineRepository.postpone(shopId, waiting.getId());// 미룬 후 rank 저장

        notification.sendEntryMessageToOthers(shopId, previousRank);
        notification.sendMessageAsPostponed(member, rank);

        return toMemberWaitingResponse(waiting, rank);
    }

    @Transactional
    public MemberWaitingResponse cancelWaiting(Member member) {
        Waiting waiting = getWaitingEntityInProgress(member);
        Shop shop = waiting.getShop();
        Owner owner = getOwnerEntity(shop);

        Long previousRank = waitingLineRepository.findRank(shop.getId(), waiting.getId());

        waitingLineRepository.cancel(shop.getId(), waiting.getId());
        waiting.changeStatusCanceled();

        notification.sendEntryMessageToOthers(shop.getId(), previousRank);
        notification.sendMessageAsCanceled(member, owner, previousRank);

        return toMemberWaitingResponse(waiting, -1L);
    }

    @Transactional(readOnly = true)
    public MemberWaitingResponse getWaiting(Member member) {
        Waiting waiting = getWaitingEntityInProgress(member);

        Shop shop = waiting.getShop();
        Long rank = waitingLineRepository.findRank(shop.getId(), waiting.getId());

        return toMemberWaitingResponse(waiting, rank);
    }

    @Transactional(readOnly = true)
    public MemberWaitingHistoryListResponse getWaitingHistory(Member member) {
        List<Waiting> waitings = waitingRepository.findWaitingWithMemberAndShop(member);
        return toMemberWaitingListResponse(waitings);
    }

    private void validateIfMemberWaitingExists(Member member) {
        if (waitingRepository.existsByMemberAndStatus(member, PROGRESS)) {
            throw new BadRequestCustomException(ALREADY_PROGRESS_WAITING_EXISTS);
        }
    }

    private Shop getShopEntity(Long shopId) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(
            () -> new NotFoundCustomException(NOT_EXIST_SHOP)
        );
        shop.validateIfShopOpened(LocalTime.now());
        return shop;
    }

    private Owner getOwnerEntity(Shop shop) {
        return ownerRepository.findOwnerByShop(shop).orElseThrow(
            () -> new NotFoundCustomException(NOT_EXIST_OWNER)
        );
    }

    private Waiting getWaitingEntityInProgress(Member member) {
        return waitingRepository.findByMemberAndStatusWithShop(member, PROGRESS)
            .orElseThrow(() -> new NotFoundCustomException(NOT_EXIST_PROGRESS_WAITING));
    }
}