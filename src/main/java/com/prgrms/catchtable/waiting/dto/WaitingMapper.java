package com.prgrms.catchtable.waiting.dto;

import static lombok.AccessLevel.PRIVATE;

import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.waiting.domain.Waiting;
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
    public static WaitingResponse toWaitingResponse(Waiting waiting, Long rank) {
        return WaitingResponse.builder()
            .createdWaitingId(waiting.getId())
            .shopId(waiting.getShop().getId())
            .shopName(waiting.getShop().getName())
            .peopleCount(waiting.getPeopleCount())
            .waitingNumber(waiting.getWaitingNumber())
            .rank(rank)
            .build();
    }
}
