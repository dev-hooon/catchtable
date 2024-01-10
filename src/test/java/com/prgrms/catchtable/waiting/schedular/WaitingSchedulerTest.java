package com.prgrms.catchtable.waiting.schedular;

import static org.assertj.core.api.Assertions.assertThat;

import com.prgrms.catchtable.waiting.repository.waitingline.WaitingLineRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Disabled
class WaitingSchedulerTest {

    @Autowired
    private WaitingLineRepository waitingLineRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @AfterEach
    void clear(){
        redisTemplate.delete("s1");
    }

    @DisplayName("특정 시간 주기로 redis 데이터를 비울 수 있다.")
    @Test
    void clearRedis() throws InterruptedException {
        Long shopId = 1L;

        waitingLineRepository.save(shopId, 1L);
        waitingLineRepository.save(shopId, 2L);
        waitingLineRepository.save(shopId, 3L);

        assertThat(waitingLineRepository.getWaitingLineSize(shopId)).isEqualTo(3);

        Thread.sleep(60*1000);
        assertThat(waitingLineRepository.getWaitingLineSize(shopId)).isZero();

    }
}