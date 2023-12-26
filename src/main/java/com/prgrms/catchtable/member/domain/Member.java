package com.prgrms.catchtable.member.domain;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.prgrms.catchtable.common.BaseEntity;
import com.prgrms.catchtable.common.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Member extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "member_name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "gender")
    @Enumerated(STRING)
    private Gender gender;

    @Column(name = "role")
    @Enumerated(STRING)
    private Role role;

    @Column(name = "date_birth")
    private LocalDate dateBirth;

    @Column(name = "notification_activated")
    private boolean notification_activated;

    @Builder
    public Member(String name, String email, String phoneNumber, Gender gender, LocalDate dateBirth) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.dateBirth = dateBirth;
        this.notification_activated = true;
        this.role = Role.MEMBER;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.getRole()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
