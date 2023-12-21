package com.prgrms.catchtable.notification.domain;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.prgrms.catchtable.common.BaseEntity;
import com.prgrms.catchtable.owner.domain.Owner;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class NotificationOwner extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "notification_owner_id")
    private Long id;

    @Column(name = "message")
    private String message;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "owner_id", foreignKey = @ForeignKey(NO_CONSTRAINT))
    private Owner owner;

    @Builder
    public NotificationOwner(String message) {
        this.message = message;
    }
}
