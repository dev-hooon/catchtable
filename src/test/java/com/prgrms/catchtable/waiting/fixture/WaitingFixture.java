package com.prgrms.catchtable.waiting.fixture;

import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.waiting.domain.Waiting;
import com.prgrms.catchtable.waiting.domain.WaitingStatus;
import com.prgrms.catchtable.waiting.dto.request.CreateWaitingRequest;
import com.prgrms.catchtable.waiting.dto.response.MemberWaitingResponse;

public class WaitingFixture {

    public static Waiting progressWaiting(Member member, Shop shop, int waitingNumber) {
        return Waiting.builder()
            .member(member)
            .shop(shop)
            .waitingNumber(waitingNumber)
            .peopleCount(2)
            .build();
    }

    public static Waiting completedWaiting(Member member, Shop shop, int waitingNumber) {
        Waiting waiting = progressWaiting(member, shop, waitingNumber);
        waiting.changeStatusCompleted();
        return waiting;
    }

    public static Waiting canceledWaiting(Member member, Shop shop, int waitingNumber) {
        Waiting waiting = progressWaiting(member, shop, waitingNumber);
        waiting.changeStatusCanceled();
        return waiting;
    }

    public static CreateWaitingRequest createWaitingRequest() {
        return CreateWaitingRequest
            .builder()
            .peopleCount(2).build();
    }

    public static MemberWaitingResponse memberWaitingResponse(int remainingPostponeCount,
        WaitingStatus status) {
        return MemberWaitingResponse.builder()
            .waitingId(1L)
            .shopId(1L)
            .shopName("shop1")
            .waitingNumber(324)
            .rank(20L)
            .peopleCount(remainingPostponeCount)
            .remainingPostponeCount(2)
            .status(status.getDescription())
            .build();
    }
}