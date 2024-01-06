package com.prgrms.catchtable.waiting.domain;

import static com.prgrms.catchtable.common.exception.ErrorCode.CAN_NOT_CANCEL_WAITING;
import static com.prgrms.catchtable.common.exception.ErrorCode.CAN_NOT_COMPLETE_WAITING;
import static com.prgrms.catchtable.common.exception.ErrorCode.POSTPONE_REMAINING_CNT_0;
import static com.prgrms.catchtable.waiting.domain.WaitingStatus.CANCELED;
import static com.prgrms.catchtable.waiting.domain.WaitingStatus.COMPLETED;
import static com.prgrms.catchtable.waiting.domain.WaitingStatus.NO_SHOW;
import static com.prgrms.catchtable.waiting.domain.WaitingStatus.PROGRESS;
import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.prgrms.catchtable.common.BaseEntity;
import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.shop.domain.Shop;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Waiting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "waiting_id")
    private Long id;

    @Column(name = "waiting_number")
    private int waitingNumber;

    @Column(name = "people_count")
    private int peopleCount;

    @Column(name = "status")
    @Enumerated(STRING)
    private WaitingStatus status;

    @Column(name = "remaining_postpone_count")
    private int remainingPostponeCount;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(NO_CONSTRAINT))
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(NO_CONSTRAINT))
    private Shop shop;

    @Builder
    public Waiting(int waitingNumber, int peopleCount, Member member, Shop shop) {
        this.waitingNumber = waitingNumber;
        this.peopleCount = peopleCount;
        this.member = member;
        this.shop = shop;
        status = PROGRESS;
        remainingPostponeCount = 2;
    }

    public void validatePostponeRemainingCount() {
        if (remainingPostponeCount == 0) {
            throw new BadRequestCustomException(POSTPONE_REMAINING_CNT_0);
        }
    }

    public void decreasePostponeRemainingCount() {
        remainingPostponeCount--;
    }

    public void completeWaiting() {
        if (status == NO_SHOW || status == CANCELED) {
            throw new BadRequestCustomException(CAN_NOT_COMPLETE_WAITING);
        }
        status = COMPLETED;
    }


    public void changeStatusCanceled() {
        if (status != PROGRESS) {
            throw new BadRequestCustomException(CAN_NOT_CANCEL_WAITING);
        }
        status = CANCELED;
    }
}
