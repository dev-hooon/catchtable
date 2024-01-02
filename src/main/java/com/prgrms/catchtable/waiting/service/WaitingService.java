package com.prgrms.catchtable.waiting.service;

import static com.prgrms.catchtable.common.exception.ErrorCode.EXISTING_MEMBER_WAITING;
import static com.prgrms.catchtable.common.exception.ErrorCode.NOT_EXIST_MEMBER;
import static com.prgrms.catchtable.common.exception.ErrorCode.NOT_EXIST_SHOP;
import static com.prgrms.catchtable.waiting.domain.WaitingStatus.PROGRESS;

import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.member.repository.MemberRepository;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.repository.ShopRepository;
import com.prgrms.catchtable.waiting.domain.Waiting;
import com.prgrms.catchtable.waiting.dto.CreateWaitingRequest;
import com.prgrms.catchtable.waiting.dto.CreateWaitingResponse;
import com.prgrms.catchtable.waiting.dto.WaitingMapper;
import com.prgrms.catchtable.waiting.repository.WaitingRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WaitingService {

    private final LocalDateTime START_DATE_TIME = LocalDateTime.of(LocalDate.now(),
        LocalTime.of(0, 0, 0));
    private final LocalDateTime END_DATE_TIME = LocalDateTime.of(LocalDate.now(),
        LocalTime.of(23, 59, 59));
    private final WaitingRepository waitingRepository;
    private final MemberRepository memberRepository;
    private final ShopRepository shopRepository;

    public CreateWaitingResponse createWaiting(Long shopId, Long memberId,
        CreateWaitingRequest request) {
        // 연관 엔티티 조회
        Member member = getMemberEntity(memberId);
        Shop shop = getShopEntity(shopId);

        // shop 영업 중인지 검증
        shop.validateIfShopOpened(LocalTime.now());

        // 기존 waiting이 있는지 검증
        validateIfMemberWaitingExists(member);

        // 대기 번호 생성
        int waitingNumber = (waitingRepository.countByShopAndCreatedAtBetween(shop,
            START_DATE_TIME, END_DATE_TIME)).intValue() + 1;

        // 대기 순서 생성
        int waitingOrder = (waitingRepository.countByShopAndStatusAndCreatedAtBetween(shop,
            PROGRESS, START_DATE_TIME, END_DATE_TIME)).intValue() + 1;

        // waiting 저장
        Waiting waiting = WaitingMapper.toWaiting(request, waitingNumber, member, shop);
        Waiting savedWaiting = waitingRepository.save(waiting);

        return WaitingMapper.toCreateWaitingResponse(savedWaiting, waitingOrder);
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
        return shopRepository.findById(shopId).orElseThrow(
            () -> new NotFoundCustomException(NOT_EXIST_SHOP)
        );
    }
}
