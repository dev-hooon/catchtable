package com.prgrms.catchtable.waiting.service;

import static com.prgrms.catchtable.common.exception.ErrorCode.EXISTING_MEMBER_WAITING;
import static com.prgrms.catchtable.common.exception.ErrorCode.NOT_EXIST_MEMBER;
import static com.prgrms.catchtable.common.exception.ErrorCode.NOT_EXIST_PROGRESS_WAITING;
import static com.prgrms.catchtable.common.exception.ErrorCode.NOT_EXIST_SHOP;
import static com.prgrms.catchtable.waiting.domain.WaitingStatus.PROGRESS;
import static com.prgrms.catchtable.waiting.dto.WaitingMapper.toWaiting;
import static com.prgrms.catchtable.waiting.dto.WaitingMapper.toWaitingResponse;

import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.member.repository.MemberRepository;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.repository.ShopRepository;
import com.prgrms.catchtable.waiting.domain.Waiting;
import com.prgrms.catchtable.waiting.dto.CreateWaitingRequest;
import com.prgrms.catchtable.waiting.dto.WaitingResponse;
import com.prgrms.catchtable.waiting.repository.WaitingRepository;
import com.prgrms.catchtable.waiting.repository.waitingline.WaitingLineRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final MemberRepository memberRepository;
    private final ShopRepository shopRepository;
    private final WaitingLineRepository waitingLineRepository;

    public WaitingResponse createWaiting(Long shopId, Long memberId,
        CreateWaitingRequest request) {
        // 연관 엔티티 조회
        Member member = getMemberEntity(memberId);
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

        return toWaitingResponse(savedWaiting, rank);
    }

    @Transactional
    public WaitingResponse postponeWaiting(Long memberId) {
        Member member = getMemberEntity(memberId);
        Waiting waiting = getWaitingEntityInProgress(member);

        Shop shop = waiting.getShop();

        waiting.validatePostponeRemainingCount();
        waitingLineRepository.postpone(shop.getId(), waiting.getId());
        Long rank = waitingLineRepository.findRank(shop.getId(), waiting.getId());
        waiting.decreasePostponeRemainingCount();
        return toWaitingResponse(waiting, rank);
    }

    @Transactional
    public WaitingResponse cancelWaiting(Long memberId) {
        Member member = getMemberEntity(memberId);
        Waiting waiting = getWaitingEntityInProgress(member);

        Shop shop = waiting.getShop();
        waitingLineRepository.cancel(shop.getId(), waiting.getId());
        waiting.changeStatusCanceled();

        return toWaitingResponse(waiting, -1L);
    }

    @Transactional(readOnly = true)
    public WaitingResponse getWaiting(Long memberId) {
        Member member = getMemberEntity(memberId);
        Waiting waiting = getWaitingEntityInProgress(member);

        Shop shop = waiting.getShop();
        Long rank = waitingLineRepository.findRank(shop.getId(), waiting.getId());

        return toWaitingResponse(waiting, rank);
    }


    private void validateIfMemberWaitingExists(Member member) {
        if (waitingRepository.existsByMember(member)) {
            throw new BadRequestCustomException(EXISTING_MEMBER_WAITING);
        }
    }

    public Member getMemberEntity(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
            () -> new NotFoundCustomException(NOT_EXIST_MEMBER)
        );
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