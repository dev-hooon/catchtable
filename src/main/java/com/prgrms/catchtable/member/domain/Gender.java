package com.prgrms.catchtable.member.domain;

import static com.prgrms.catchtable.common.exception.ErrorCode.*;

import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {
    MALE("male"),
    FEMALE("female");

    private final String type;

    public static Gender of(String input){
        return Arrays.stream(values())
            .filter(gender -> gender.isEqual(input))
            .findAny()
            .orElseThrow(() -> new BadRequestCustomException(BAD_REQUEST_INPUT_GENDER_TYPE));
    }

    private boolean isEqual(String input){
        return input.equals(this.type);
    }
}
