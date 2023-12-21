package com.prgrms.catchtable.shop.domain;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class Address {

    @Column(name = "city")
    private String city;

    @Column(name = "district")
    private String district;

    @Builder
    public Address(String city, String district) {
        this.city = city;
        this.district = district;
    }
}
