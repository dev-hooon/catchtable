package com.prgrms.catchtable.waiting.fixture;

import static com.prgrms.catchtable.waiting.domain.WaitingStatus.*;

import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.waiting.domain.Waiting;
import com.prgrms.catchtable.waiting.domain.WaitingStatus;
import java.time.LocalDateTime;
import org.springframework.test.util.ReflectionTestUtils;

public class WaitingFixture {

    public static Waiting waiting(Member member, Shop shop, int waitingNumber) {
        return Waiting.builder()
            .member(member)
            .shop(shop)
            .waitingNumber(waitingNumber)
            .peopleCount(2)
            .build();
    }

    public static Waiting completedWaiting(Member member, Shop shop, int waitingNumber) {
        Waiting waiting = waiting(member, shop, waitingNumber);
        ReflectionTestUtils.setField(waiting, "status", COMPLETED) ;
        return waiting;
    }
}