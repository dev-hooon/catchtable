package com.prgrms.catchtable.common.notification;

import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationContent {
    RESERVATION_COMPLETED(time -> time.concat(" 시간의 예약이 완료 되었습니다.")),
    RESERVATION_CANCELLED(time -> time.concat(" 시간의 예약이 취소 되었습니다")),
    RESERVATION_ONE_HOUR_LEFT(time -> time.concat(" 시간 예약까지 한시간 남았습니다")),
    RESERVATION_TIME_OUT(time -> "예약 시간이 되었습니다. 입장해주세요.");


    private final Function<String, String> expression;

    public String apply(String time){
        return expression.apply(time);
    }

}
