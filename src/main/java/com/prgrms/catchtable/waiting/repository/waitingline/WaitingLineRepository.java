package com.prgrms.catchtable.waiting.repository.waitingline;

import java.util.List;

public interface WaitingLineRepository {

    void save(Long shopId, Long waitingId);

    Long entry(Long shopId);

    void cancel(Long shopId, Long waitingId);

    void postpone(Long shopId, Long waitingId);

    Long findRank(Long shopId, Long waitingId);

    Long findRankValue(Long shopId, int rank);

    Long getWaitingLineSize(Long shopId);

    List<Long> getShopWaitingIdsInOrder(Long shopId);

    void printWaitingLine(Long shopId);
}
