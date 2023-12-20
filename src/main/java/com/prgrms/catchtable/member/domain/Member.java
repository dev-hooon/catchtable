package com.prgrms.catchtable.member.domain;

import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.prgrms.catchtable.notification.domain.NotificationMember;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "member_name")
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "gender")
    @Enumerated(STRING)
    private Gender gender;

    @Column(name = "date_birth")
    private LocalDate dateBirth;

    @Column(name = "notification_activated")
    private boolean notification_activated;

    @OneToMany(mappedBy = "member", cascade = REMOVE, orphanRemoval = true)
    private List<NotificationMember> notificationMemberList;

    @Builder
    public Member(String name, String phoneNumber, Gender gender, LocalDate dateBirth,
        boolean notification_activated) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.dateBirth = dateBirth;
        this.notification_activated = notification_activated;
    }
}
