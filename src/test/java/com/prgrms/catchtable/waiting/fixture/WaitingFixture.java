package com.prgrms.catchtable.waiting.fixture;

import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.waiting.domain.Waiting;

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
}