package com.prgrms.catchtable.waiting.service;

import static com.prgrms.catchtable.common.exception.ErrorCode.ALREADY_PROGRESS_WAITING_EXISTS;
import static com.prgrms.catchtable.common.exception.ErrorCode.NOT_EXIST_PROGRESS_WAITING;
import static com.prgrms.catchtable.common.exception.ErrorCode.NOT_EXIST_SHOP;
import static com.prgrms.catchtable.common.notification.WaitingNotificationContent.CANCELED;
import static com.prgrms.catchtable.common.notification.WaitingNotificationContent.REGISTERED;
import static com.prgrms.catchtable.common.notification.WaitingNotificationContent.THIRD_RANK;
import static com.prgrms.catchtable.waiting.domain.WaitingStatus.PROGRESS;
import static com.prgrms.catchtable.waiting.dto.WaitingMapper.toMemberWaitingListResponse;
import static com.prgrms.catchtable.waiting.dto.WaitingMapper.toMemberWaitingResponse;
import static com.prgrms.catchtable.waiting.dto.WaitingMapper.toWaiting;

import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.notification.dto.request.SendMessageToMemberRequest;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.repository.ShopRepository;
import com.prgrms.catchtable.waiting.domain.Waiting;
import com.prgrms.catchtable.waiting.domain.WaitingStatus;
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
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher publisher;

    @Transactional
    public MemberWaitingResponse createWaiting(Long shopId, Member member,
        CreateWaitingRequest request) {
        // 연관 엔티티 조회
        Shop shop = getShopEntity(shopId);

        // 기존 waiting이 있는지 검증
        validateIfMemberWaitingExists(member);

        // 대기 번호 생성
        int waitingNumber = (waitingRepository.countByShopAndCreatedAtBetween(shop,
            START_DATE_TIME, END_DATE_TIME)).intValue() + 1;

        // waiting 저장
        Waiting waiting = toWaiting(request, waitingNumber, member, shop);
        Waiting savedWaiting = waitingRepository.save(waiting);

        waitingLineRepository.save(shopId, waiting.getId());
        Long rank = waitingLineRepository.findRank(shopId, waiting.getId());
        sendMessageToMember(member, PROGRESS, rank);

        return toMemberWaitingResponse(savedWaiting, rank);
    }

    @Transactional
    public MemberWaitingResponse postponeWaiting(Member member) {
        Waiting waiting = getWaitingEntityInProgress(member);
        Shop shop = waiting.getShop();
        Long previousRank = waitingLineRepository.findRank(shop.getId(), waiting.getId());

        waiting.decreasePostponeRemainingCount();
        waitingLineRepository.postpone(shop.getId(), waiting.getId());
        Long rank = waitingLineRepository.findRank(shop.getId(), waiting.getId());
        if (previousRank <= 3) {
            sendMessageToThirdRankMember(shop.getId());
        }
        return toMemberWaitingResponse(waiting, rank);
    }

    @Transactional
    public MemberWaitingResponse cancelWaiting(Member member) {
        Waiting waiting = getWaitingEntityInProgress(member);
        Shop shop = waiting.getShop();
        Long previousRank = waitingLineRepository.findRank(shop.getId(), waiting.getId());

        waitingLineRepository.cancel(shop.getId(), waiting.getId());
        waiting.changeStatusCanceled();

        sendMessageToMember(member, WaitingStatus.CANCELED, -1L);
        if (previousRank <= 3) {
            sendMessageToThirdRankMember(shop.getId());
        }
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
    public MemberWaitingHistoryListResponse getMemberWaitingHistory(Member member) {
        List<Waiting> waitings = waitingRepository.findWaitingWithMemberAndShop(member);
        return toMemberWaitingListResponse(waitings);
    }

    public void sendMessageToThirdRankMember(Long shopId) {
        Long thirdRankWaitingId = waitingLineRepository.findThirdRankValue(shopId);
        if (thirdRankWaitingId != null) {
            Member thirdRankMember = waitingRepository.findWaitingWithMember(thirdRankWaitingId)
                .getMember();
            SendMessageToMemberRequest request = SendMessageToMemberRequest.builder()
                .member(thirdRankMember)
                .content(THIRD_RANK.getMessage())
                .build();
            publisher.publishEvent(request);
        }
    }

    public void sendMessageToMember(Member member, WaitingStatus status, Long rank) {
        StringBuilder content = new StringBuilder();
        if (status == PROGRESS) {
            content.append(String.format(REGISTERED.getMessage(), rank));
        } else {
            content.append(CANCELED.getMessage());
        }
        SendMessageToMemberRequest request = SendMessageToMemberRequest.builder()
            .member(member)
            .content(content.toString())
            .build();
        publisher.publishEvent(request);
    }

    private void validateIfMemberWaitingExists(Member member) {
        if (waitingRepository.existsByMemberAndStatus(member, PROGRESS)) {
            throw new BadRequestCustomException(ALREADY_PROGRESS_WAITING_EXISTS);
        }
    }

    public Shop getShopEntity(Long shopId) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(
            () -> new NotFoundCustomException(NOT_EXIST_SHOP)
        );
        shop.validateIfShopOpened(LocalTime.now());
        return shop;
    }

    public Waiting getWaitingEntityInProgress(Member member) {
        return waitingRepository.findByMemberAndStatusWithShop(member, PROGRESS)
            .orElseThrow(() -> new NotFoundCustomException(NOT_EXIST_PROGRESS_WAITING));
    }
}