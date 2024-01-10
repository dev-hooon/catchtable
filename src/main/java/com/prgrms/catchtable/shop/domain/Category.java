package com.prgrms.catchtable.shop.domain;

import com.prgrms.catchtable.common.exception.ErrorCode;
import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    KOREAN_FOOD("한식"),
    JAPANESE_FOOD("일식"),
    CHINESE_FOOD("중식"),
    WESTERN_FOOD("양식");

    private final String type;

    public static Category of(String input) {
        return Arrays.stream(values())
            .filter(category -> category.isEqual(input))
            .findAny()
            .orElseThrow(() -> new BadRequestCustomException(ErrorCode.INVALID_INPUT_TYPE));
    }

    private boolean isEqual(String input) {
        return type.equals(input);
    }
}
