package com.prgrms.catchtable.shop.domain;

import static com.prgrms.catchtable.common.exception.ErrorCode.SHOP_NOT_RUNNING;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.prgrms.catchtable.common.BaseEntity;
import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Shop extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "shop_id")
    private Long id;

    @Column(name = "shop_name")
    private String name;

    @Column(name = "rating")
    private BigDecimal rating;

    @Column(name = "category")
    @Enumerated(STRING)
    private Category category;

    @Embedded
    private Address address;

    @Column(name = "capacity")
    private int capacity;

    @Column(name = "opening_time")
    private LocalTime openingTime;

    @Column(name = "closing_time")
    private LocalTime closingTime;

    @BatchSize(size = 30)
    @OneToMany(mappedBy = "shop")
    private List<ReservationTime> reservationTimeList = new ArrayList<>();

    @Builder
    public Shop(String name, BigDecimal rating, Category category, Address address, int capacity,
        LocalTime openingTime, LocalTime closingTime) {
        this.name = name;
        this.rating = rating;
        this.category = category;
        this.address = address;
        this.capacity = capacity;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    public void validateIfShopOpened(LocalTime localTime) {
        if (localTime.isBefore(openingTime) || localTime.isAfter(closingTime)) {
            throw new BadRequestCustomException(SHOP_NOT_RUNNING);
        }
    }
}
