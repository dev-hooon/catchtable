package com.prgrms.catchtable.jwt.token;

import com.prgrms.catchtable.common.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Token {

    private String accessToken;

    private String refreshToken;

    private String email;

    private Role role;
}
