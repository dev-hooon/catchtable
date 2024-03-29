package com.prgrms.catchtable.waiting.dto;

import static lombok.AccessLevel.PRIVATE;

import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.waiting.domain.Waiting;
import com.prgrms.catchtable.waiting.dto.request.CreateWaitingRequest;
import com.prgrms.catchtable.waiting.dto.response.MemberWaitingHistoryListResponse;
import com.prgrms.catchtable.waiting.dto.response.MemberWaitingHistoryResponse;
import com.prgrms.catchtable.waiting.dto.response.MemberWaitingResponse;
import com.prgrms.catchtable.waiting.dto.response.OwnerWaitingListResponse;
import com.prgrms.catchtable.waiting.dto.response.OwnerWaitingResponse;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class WaitingMapper {

    // dto -> entity
    public static Waiting toWaiting(CreateWaitingRequest request, int waitingNumber, Member member,
        Shop shop) {
        return Waiting.builder()
            .waitingNumber(waitingNumber)
            .peopleCount(request.peopleCount())
            .member(member)
            .shop(shop).build();
    }

    // entity -> dto
    public static MemberWaitingResponse toMemberWaitingResponse(Waiting waiting, Long rank) {
        return MemberWaitingResponse.builder()
            .waitingId(waiting.getId())
            .shopId(waiting.getShop().getId())
            .shopName(waiting.getShop().getName())
            .peopleCount(waiting.getPeopleCount())
            .waitingNumber(waiting.getWaitingNumber())
            .rank(rank)
            .remainingPostponeCount(waiting.getRemainingPostponeCount())
            .status(waiting.getStatus().getDescription())
            .build();
    }

    public static MemberWaitingHistoryResponse toMemberWaitingHistoryResponse(Waiting waiting) {
        return MemberWaitingHistoryResponse.builder()
            .waitingId(waiting.getId())
            .shopId(waiting.getShop().getId())
            .shopName(waiting.getShop().getName())
            .peopleCount(waiting.getPeopleCount())
            .status(waiting.getStatus().getDescription())
            .build();
    }

    public static MemberWaitingHistoryListResponse toMemberWaitingListResponse(
        List<Waiting> waitings) {
        return new MemberWaitingHistoryListResponse(waitings.stream()
            .map(WaitingMapper::toMemberWaitingHistoryResponse)
            .toList());
    }

    public static OwnerWaitingResponse toOwnerWaitingResponse(Waiting waiting, Long rank) {
        return OwnerWaitingResponse.builder()
            .waitingId(waiting.getId())
            .waitingNumber(waiting.getWaitingNumber())
            .rank(rank)
            .peopleCount(waiting.getPeopleCount())
            .status(waiting.getStatus().getDescription())
            .build();
    }

    public static OwnerWaitingListResponse toOwnerWaitingListResponse(List<Waiting> waitings) {
        AtomicLong rank = new AtomicLong(1);
        return new OwnerWaitingListResponse(
            waitings.stream()
                .map(waiting -> toOwnerWaitingResponse(waiting, rank.getAndIncrement()))
                .toList()
        );
    }
}
