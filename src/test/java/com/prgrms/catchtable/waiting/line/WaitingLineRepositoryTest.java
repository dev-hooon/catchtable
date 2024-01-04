package com.prgrms.catchtable.waiting.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WaitingLineRepositoryTest {

    private final WaitingLineRepository waitingLineRepository = new WaitingLineRepository();


    @AfterEach()
    void clear() {
        waitingLineRepository.clear();
    }

    @DisplayName("입장 시 뒤 대기 순서 1씩 앞당겨짐")
    @Test
    void entry() {
        //given
        Long shopId = 1L;

        waitingLineRepository.save(shopId, 1L);
        waitingLineRepository.save(shopId, 2L);
        waitingLineRepository.save(shopId, 3L);

        //when
        waitingLineRepository.entry(1L, 1L);

        //then
        assertThat(waitingLineRepository.findRank(1L, 1L))
            .isEqualTo(-1);
        assertThat(waitingLineRepository.findRank(1L, 2L))
            .isEqualTo(1);
        assertThat(waitingLineRepository.findRank(1L, 3L))
            .isEqualTo(2);
    }

    @DisplayName("저장 후 대기순서를 가져올 수 있다.")
    @Test
    void save() {
        //given
        Long shopId = 1L;
        Long waitingId = 1L;
        //when
        waitingLineRepository.save(shopId, waitingId);
        //then
        int waitingOrder = waitingLineRepository.findRank(1L, 1L);
        assertThat(waitingOrder).isEqualTo(1);
    }

    @DisplayName("지연 시 대기 순서가 맨 뒤가 된다. 뒷 순서의 요소들은 한 칸씩 앞으로 이동한다.")
    @Test
    void postpone() {
        //given
        Long shopId = 1L;

        waitingLineRepository.save(shopId, 1L);
        waitingLineRepository.save(shopId, 2L);
        waitingLineRepository.save(shopId, 3L);

        //when
        waitingLineRepository.postpone(1L, 1L);

        //then
        assertThat(waitingLineRepository.findRank(1L, 1L))
            .isEqualTo(3);
        assertThat(waitingLineRepository.findRank(1L, 2L))
            .isEqualTo(1);
        assertThat(waitingLineRepository.findRank(1L, 3L))
            .isEqualTo(2);

    }

    @DisplayName("맨 뒤에 있을 시 대기 지연을 할 수 없다.")
    @Test
    void postpone_fails() {
        //given
        Long shopId = 1L;

        waitingLineRepository.save(shopId, 1L);
        waitingLineRepository.save(shopId, 2L);
        waitingLineRepository.save(shopId, 3L);

        //when, then
        assertThrows(BadRequestCustomException.class,
            () -> waitingLineRepository.postpone(shopId, 3L));
    }

    @DisplayName("대기 취소를 할 수 있다.")
    @Test
    void cancel() {
        //given
        Long shopId = 1L;

        waitingLineRepository.save(shopId, 1L);
        waitingLineRepository.save(shopId, 2L);
        waitingLineRepository.save(shopId, 3L);

        //when
        waitingLineRepository.cancel(1L, 1L);
        //then
        assertThat(waitingLineRepository.findRank(1L, 1L))
            .isEqualTo(-1);
        assertThat(waitingLineRepository.findRank(1L, 2L))
            .isEqualTo(1);
        assertThat(waitingLineRepository.findRank(1L, 3L))
            .isEqualTo(2);
    }

}