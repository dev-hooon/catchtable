package com.prgrms.catchtable.waiting.schedular;

import static com.prgrms.catchtable.waiting.domain.WaitingStatus.CANCELED;
import static com.prgrms.catchtable.waiting.domain.WaitingStatus.PROGRESS;

import com.prgrms.catchtable.shop.repository.ShopRepository;
import com.prgrms.catchtable.waiting.repository.WaitingRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class WaitingScheduler {

    private final StringRedisTemplate redisTemplate;

    private final WaitingRepository waitingRepository;
    private final ShopRepository shopRepository;

    // 매일 자정 레디스 데이터 비우기
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void clearRedis() {
        Set<String> keys = redisTemplate.keys("s*");
        redisTemplate.delete(keys);
    }

    // 매일 자정 대기 상태 바꾸기
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void updateProgressStatus() {
        waitingRepository.updateWaitingStatus(CANCELED, PROGRESS);
    }

    // 매일 자정 대기 수 초기화
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void initWaitingCount(){
        shopRepository.initWaitingCount();
    }
}
