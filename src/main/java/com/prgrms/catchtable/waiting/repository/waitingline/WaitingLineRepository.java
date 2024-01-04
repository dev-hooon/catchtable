package com.prgrms.catchtable.waiting.repository.waitingline;

import static com.prgrms.catchtable.common.exception.ErrorCode.ALREADY_END_LINE;
import static com.prgrms.catchtable.common.exception.ErrorCode.CAN_NOT_ENTRY;
import static com.prgrms.catchtable.common.exception.ErrorCode.WAITING_DOES_NOT_EXIST;

import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WaitingLineRepository {

    public final Map<Long, Queue<Long>> waitingLines = new ConcurrentHashMap<>();

    public void save(Long shopId, Long waitingId) {
        Queue<Long> waitingLine = waitingLines.computeIfAbsent(shopId, k -> new LinkedList<>());
        waitingLine.add(waitingId);
    }

    public void entry(Long shopId, Long waitingId) {
        Queue<Long> waitingLine = waitingLines.get(shopId);
        if (!Objects.equals(waitingLine.peek(), waitingId)) {
            throw new BadRequestCustomException(CAN_NOT_ENTRY);
        }
        waitingLine.remove();
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

    public int findRank(Long shopId, Long waitingId) {
        Queue<Long> waitingLine = waitingLines.get(shopId);

        int index = 0;
        for (Long waitingIdInLine : waitingLine) {
            if (Objects.equals(waitingIdInLine, waitingId)) {
                return index + 1;
            }
            index++;
        }
        return -1;
    }

    public int getWaitingLineSize(Long shopId) { //postpone에서 사용
        Queue<Long> waitingLine = waitingLines.get(shopId);
        return waitingLine != null ? waitingLine.size() : 0;
    }

    public void validateIfWaitingExists(Queue<Long> waitingLine, Long waitingId) {
        if (!waitingLine.contains(waitingId)) {
            throw new NotFoundCustomException(WAITING_DOES_NOT_EXIST);
        }
    }

    private void validateIfPostponeAvailable(Long shopId, Long waitingId) {
        if (findRank(shopId, waitingId) == getWaitingLineSize(shopId)) {
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
