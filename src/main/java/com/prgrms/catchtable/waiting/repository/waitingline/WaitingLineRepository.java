package com.prgrms.catchtable.waiting.repository.waitingline;

public interface WaitingLineRepository {
    void save(Long shopId, Long waitingId);
    void entry(Long shopId, Long waitingId);
    void cancel(Long shopId, Long waitingId);
    void postpone(Long shopId, Long waitingId);
    Long findRank(Long shopId, Long waitingId);
    Long getWaitingLineSize(Long shopId);
    void printWaitingLine(Long shopId);
}