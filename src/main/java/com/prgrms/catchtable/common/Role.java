package com.prgrms.catchtable.common;

import static com.prgrms.catchtable.common.exception.ErrorCode.INVALID_INPUT_TYPE;

import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    MEMBER("ROLE_MEMBER"),
    OWNER("ROLE_OWNER");

    private final String role;

    public static Role of(String type) {
        return Arrays.stream(values())
            .filter(role -> role.isEqual(type))
            .findAny()
            .orElseThrow(() -> new BadRequestCustomException(INVALID_INPUT_TYPE));
    }

    private boolean isEqual(String input) {
        return input.equals(this.role);
    }
}
