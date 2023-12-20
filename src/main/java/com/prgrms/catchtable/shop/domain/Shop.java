package com.prgrms.catchtable.shop.domain;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Shop {

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

    @Builder
    public Shop(String name, BigDecimal rating, Category category, Address address, int capacity) {
        this.name = name;
        this.rating = rating;
        this.category = category;
        this.address = address;
        this.capacity = capacity;
    }
}
