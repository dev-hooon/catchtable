package com.prgrms.catchtable.reservation.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationLockRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public Boolean lock(Long key) {
        return redisTemplate
            .opsForValue()
            .setIfAbsent(key.toString(), "lock");
    }

    public Boolean unlock(Long key) {
        return redisTemplate
            .delete(key.toString());
    }

}
