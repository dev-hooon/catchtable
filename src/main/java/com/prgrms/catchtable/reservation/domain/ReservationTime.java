package com.prgrms.catchtable.reservation.domain;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.prgrms.catchtable.shop.domain.Shop;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class ReservationTime {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "reservation_time_id")
    private Long id;

    @Column(name = "time")
    private LocalDateTime time;

    @Column(name = "is_occupied")
    private boolean isOccupied;

    @Column(name = "is_pre_occupied")
    private boolean isPreOccupied;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Shop shop;

    @Builder
    public ReservationTime(LocalDateTime time) {
        this.time = time;
        this.isOccupied = false;
    }

    public void reverseOccupied() {
        this.isOccupied = !this.isOccupied;
    }

    public void reversePreOccupied() {
        this.isPreOccupied = !this.isPreOccupied;
    }

    public void insertShop(Shop shop){
        this.shop = shop;
    }
}
