package com.prgrms.catchtable.reservation.domain;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.prgrms.catchtable.common.BaseEntity;
import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.reservation.dto.request.ModifyReservationRequest;
import com.prgrms.catchtable.shop.domain.Shop;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
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
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @Enumerated(STRING)
    @Column(name = "status")
    private ReservationStatus status;

    @Column(name = "people_count")
    private int peopleCount;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Shop shop;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "reservation_time_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ReservationTime reservationTime;

    @Builder
    public Reservation(ReservationStatus status, int peopleCount,
        ReservationTime reservationTime) {
        this.status = status;
        this.peopleCount = peopleCount;
        this.reservationTime = reservationTime;
        this.shop = reservationTime.getShop();
    }

    public void modifyReservation(ReservationTime reservationTime, int peopleCount){
        this.reservationTime = reservationTime;
        this.peopleCount = peopleCount;
    }

}
