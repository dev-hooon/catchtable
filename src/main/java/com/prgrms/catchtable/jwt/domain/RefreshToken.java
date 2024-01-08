package com.prgrms.catchtable.jwt.domain;

import static jakarta.persistence.EnumType.*;
import static lombok.AccessLevel.PROTECTED;

import com.prgrms.catchtable.common.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token")
    private String token;

    @Column(name = "email")
    private String email;

    @Column(name = "role")
    @Enumerated(STRING)
    private Role role;

    @Builder
    public RefreshToken(String token, String email, Role role) {
        this.token = token;
        this.email = email;
        this.role = role;
    }

}
