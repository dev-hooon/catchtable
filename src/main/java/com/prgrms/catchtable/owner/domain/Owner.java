package com.prgrms.catchtable.owner.domain;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.prgrms.catchtable.common.BaseEntity;
import com.prgrms.catchtable.member.domain.Gender;
import com.prgrms.catchtable.shop.domain.Shop;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Owner extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "owner_id")
    private Long id;

    @Column(name = "owner_name")
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "gender")
    @Enumerated(STRING)
    private Gender gender;

    @Column(name = "date_birth")
    private LocalDate dateBirth;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "shop_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Shop shop;

    @Builder
    public Owner(String name, String phoneNumber, Gender gender, LocalDate dateBirth) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.dateBirth = dateBirth;
    }

    public void insertShop(Shop shop){
        this.shop = shop;
    }
}
