package com.prgrms.catchtable.waiting.repository.waitingline;

import static com.prgrms.catchtable.common.exception.ErrorCode.ALREADY_END_LINE;
import static com.prgrms.catchtable.common.exception.ErrorCode.WAITING_DOES_NOT_EXIST;

import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BasicWaitingLineRepository implements WaitingLineRepository {

    public final Map<Long, Queue<Long>> waitingLines = new ConcurrentHashMap<>();

    public void save(Long shopId, Long waitingId) {
        Queue<Long> waitingLine = waitingLines.computeIfAbsent(shopId, k -> new LinkedList<>());
        waitingLine.add(waitingId);
    }

    public Long entry(Long shopId) {
        Queue<Long> waitingLine = waitingLines.get(shopId);
        return waitingLine.remove();
    }

    public List<Long> getShopWaitingIdsInOrder(Long shopId) {
        Queue<Long> waitingLine = waitingLines.get(shopId);
        return waitingLine.stream().toList();
    }

    public void cancel(Long shopId, Long waitingId) {
        Queue<Long> waitingLine = waitingLines.get(shopId);
        validateIfWaitingExists(waitingLine, waitingId);
        for (Long waitingIdInLine : waitingLine) {
            if (Objects.equals(waitingIdInLine, waitingId)) {
                waitingLine.remove(waitingIdInLine);
                break;
            }
        }
    }

    public void postpone(Long shopId, Long waitingId) {
        Queue<Long> waitingLine = waitingLines.get(shopId);
        validateIfWaitingExists(waitingLine, waitingId);
        validateIfPostponeAvailable(shopId, waitingId);
        for (Long waitingIdInLine : waitingLine) {
            if (Objects.equals(waitingIdInLine, waitingId)) {
                waitingLine.remove(waitingIdInLine);
                waitingLine.add(waitingIdInLine);
                break;
            }
        }
    }

    public Long findRank(Long shopId, Long waitingId) {
        Queue<Long> waitingLine = waitingLines.get(shopId);

        int index = 0;
        for (Long waitingIdInLine : waitingLine) {
            if (Objects.equals(waitingIdInLine, waitingId)) {
                return (long) index + 1;
            }
            index++;
        }
        return -1L;
    }

    public Long findRankValue(Long shopId,int rank) {
        Queue<Long> waitingLine = waitingLines.get(shopId);
        int index = 0;
        for (Long element : waitingLine) {
            if (index == rank-1) {
                return element;
            }
            index++;
        }
        return null;
    }

    public Long getWaitingLineSize(Long shopId) { //postpone에서 사용
        Queue<Long> waitingLine = waitingLines.get(shopId);
        return (long) (waitingLine != null ? waitingLine.size() : 0);
    }

    public void validateIfWaitingExists(Queue<Long> waitingLine, Long waitingId) {
        if (!waitingLine.contains(waitingId)) {
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

    public void clear() {
        waitingLines.clear();
    }

    public void printWaitingLine(Long shopId) {
        Queue<Long> waitingLine = waitingLines.get(shopId);
        if (waitingLine != null) {
            log.info("Queue: {}", waitingLine);
        } else {
            log.warn("Queue is empty or not found for Shop ID: {}", shopId);
        }
    }
}
