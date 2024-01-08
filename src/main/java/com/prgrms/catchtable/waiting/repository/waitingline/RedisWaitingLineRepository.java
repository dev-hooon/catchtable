package com.prgrms.catchtable.waiting.repository.waitingline;

import static com.prgrms.catchtable.common.exception.ErrorCode.ALREADY_END_LINE;
import static com.prgrms.catchtable.common.exception.ErrorCode.WAITING_DOES_NOT_EXIST;

import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Primary
@Component
public class RedisWaitingLineRepository implements WaitingLineRepository {

    private final StringRedisTemplate redisTemplate;

    public void save(Long shopId, Long waitingId) {
        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations)
                throws DataAccessException {
                try {
                    operations.multi();
                    redisTemplate.opsForList().leftPush("s" + shopId, waitingId.toString());
                    return operations.exec();
                } catch (Exception e) {
                    operations.discard();
                }
                return operations.exec();
            }
        });
    }

    public List<Long> getShopWaitingIdsInOrder(Long shopId) {
        List<String> stringList = redisTemplate.opsForList().range("s" + shopId, 0, -1);
        if (stringList == null) {
            throw new BadRequestCustomException(WAITING_DOES_NOT_EXIST);
        }
        List<Long> longList = new ArrayList<>(stringList.stream()
            .map(Long::parseLong)
            .toList());
        Collections.reverse(longList);
        return longList;
    }

    public void entry(Long shopId, Long waitingId) {
        validateIfWaitingExists(shopId, waitingId);
        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations)
                throws DataAccessException {
                try {
                    operations.multi();
                    redisTemplate.opsForList().rightPop("s" + shopId);
                    return operations.exec();
                } catch (Exception e) {
                    operations.discard();
                }
                return operations.exec();
            }
        });
    }

    public void cancel(Long shopId, Long waitingId) {
        validateIfWaitingExists(shopId, waitingId);
        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations)
                throws DataAccessException {
                try {
                    operations.multi();
                    redisTemplate.opsForList().remove("s" + shopId, 1, waitingId.toString());
                    return operations.exec();
                } catch (Exception e) {
                    operations.discard();
                }
                return operations.exec();
            }
        });
    }

    public void postpone(Long shopId, Long waitingId) {
        validateIfWaitingExists(shopId, waitingId);
        validateIfPostponeAvailable(shopId, waitingId);

        if (Objects.equals(findRank(shopId, waitingId), getWaitingLineSize(shopId))) {
            throw new BadRequestCustomException(ALREADY_END_LINE);
        }
        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations)
                throws DataAccessException {
                try {
                    operations.multi();
                    redisTemplate.opsForList().rightPop("s" + shopId);
                    redisTemplate.opsForList().leftPush("s" + shopId, waitingId.toString());
                    return operations.exec();
                } catch (Exception e) {
                    operations.discard();
                }
                return operations.exec();
            }
        });
    }

    public Long findRank(Long shopId, Long waitingId) {
        Long index = redisTemplate.opsForList().indexOf("s" + shopId, waitingId.toString());
        if (index == null) {
            throw new NotFoundCustomException(WAITING_DOES_NOT_EXIST);
        }
        return getWaitingLineSize(shopId) - index;
    }

    public Long getWaitingLineSize(Long shopId) {
        return redisTemplate.opsForList().size("s" + shopId);
    }

    public void validateIfWaitingExists(Long shopId, Long waitingId) {
        Long index = redisTemplate.opsForList().indexOf("s" + shopId, waitingId.toString());
        if (index == null) {
            throw new NotFoundCustomException(WAITING_DOES_NOT_EXIST);
        }
    }

    private void validateIfPostponeAvailable(Long shopId, Long waitingId) {
        if (Objects.equals(findRank(shopId, waitingId), getWaitingLineSize(shopId))) {
            {
                throw new BadRequestCustomException(ALREADY_END_LINE);
            }
        }
    }

    public void printWaitingLine(Long shopId) {
        List<String> waitingLine = redisTemplate.opsForList().range("s" + shopId, 0, -1);
        if (waitingLine != null) {
            log.info("Queue: {}", waitingLine);
        } else {
            log.warn("Queue is empty or not found for Shop ID: {}", shopId);
        }
    }
}
