package com.prgrms.catchtable.waiting.repository.waitingline;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BasicWaitingLineRepositoryTest {

    private final BasicWaitingLineRepository repository = new BasicWaitingLineRepository();


    @AfterEach()
    void clear() {
        repository.clear();
    }

    @DisplayName("입장 시 뒤 대기 순서 1씩 앞당겨짐")
    @Test
    void entry() {
        //given
        Long shopId = 1L;

        repository.save(shopId, 1L);
        repository.save(shopId, 2L);
        repository.save(shopId, 3L);

        //when
        Long enteredWaitingId = repository.entry(1L);

        //then
        assertThat(enteredWaitingId).isEqualTo(1L);
        assertThat(repository.findRank(1L, 1L))
            .isEqualTo(-1);
        assertThat(repository.findRank(1L, 2L))
            .isEqualTo(1);
        assertThat(repository.findRank(1L, 3L))
            .isEqualTo(2);
    }

    @DisplayName("저장 후 대기순서를 가져올 수 있다.")
    @Test
    void save() {
        //given
        Long shopId = 1L;
        Long waitingId = 1L;
        //when
        repository.save(shopId, waitingId);
        //then
        Long waitingOrder = repository.findRank(1L, 1L);
        assertThat(waitingOrder).isEqualTo(1L);
    }

    @DisplayName("지연 시 대기 순서가 맨 뒤가 된다. 뒷 순서의 요소들은 한 칸씩 앞으로 이동한다.")
    @Test
    void postpone() {
        //given
        Long shopId = 1L;

        repository.save(shopId, 1L);
        repository.save(shopId, 2L);
        repository.save(shopId, 3L);

        //when
        repository.postpone(1L, 1L);

        //then
        assertThat(repository.findRank(1L, 1L))
            .isEqualTo(3);
        assertThat(repository.findRank(1L, 2L))
            .isEqualTo(1);
        assertThat(repository.findRank(1L, 3L))
            .isEqualTo(2);

    }

    @DisplayName("맨 뒤에 있을 시 대기 지연을 할 수 없다.")
    @Test
    void postpone_fails() {
        //given
        Long shopId = 1L;

        repository.save(shopId, 1L);
        repository.save(shopId, 2L);
        repository.save(shopId, 3L);

        //when, then
        assertThrows(BadRequestCustomException.class,
            () -> repository.postpone(shopId, 3L));
    }

    @DisplayName("대기 취소를 할 수 있다.")
    @Test
    void cancel() {
        //given
        Long shopId = 1L;

        repository.save(shopId, 1L);
        repository.save(shopId, 2L);
        repository.save(shopId, 3L);

        //when
        repository.cancel(1L, 1L);
        //then
        assertThat(repository.findRank(1L, 1L))
            .isEqualTo(-1);
        assertThat(repository.findRank(1L, 2L))
            .isEqualTo(1);
        assertThat(repository.findRank(1L, 3L))
            .isEqualTo(2);
    }

    @DisplayName("특정 가게의 웨이팅 아이디를 rank 순으로 가져온다.")
    @Test
    void getShopWaitingIdOrder() {
        //given
        Long shopId = 1L;
        repository.save(shopId, 1L);
        repository.save(shopId, 2L);
        repository.save(shopId, 3L);
        //when
        List<Long> waitingIds = repository.getShopWaitingIdsInOrder(shopId);
        //then
        assertThat(waitingIds.get(0)).isEqualTo(1L);
        assertThat(waitingIds.get(1)).isEqualTo(2L);
        assertThat(waitingIds.get(2)).isEqualTo(3L);
    }

    @DisplayName("웨이팅 3번째 waitingId 반환한다.")
    @Test
    void findThirdRankValue() {
        //given
        Long shopId = 1L;
        repository.save(shopId, 1L);
        repository.save(shopId, 2L);
        repository.save(shopId, 3L);
        //when
        Long waitingId = repository.findRankValue(shopId, 3);
        //then
        assertThat(waitingId).isEqualTo(3L);
    }

    @DisplayName("웨이팅 3번째 waitingId 없으면 null을 반환한다.")
    @Test
    void findThirdRankValueNull() {
        //given
        Long shopId = 1L;
        repository.save(shopId, 1L);
        repository.save(shopId, 2L);
        //when
        Long waitingId = repository.findRankValue(shopId, 3);
        //then
        assertThat(waitingId).isNull();
    }

}