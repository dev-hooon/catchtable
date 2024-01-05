package com.prgrms.catchtable.waiting.repository.waitingline;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class RedisWaitingLineRepositoryTest {

    @Autowired
    private RedisWaitingLineRepository repository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @AfterEach
    void clear() {
        redisTemplate.delete("s1");
    }

    @DisplayName("큐에 웨이팅을 추가한 후 순서를 반환받을 수 있다.")
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

    @DisplayName("입장 시 뒤 대기 순서 1씩 앞당겨짐")
    @Test
    void entry() {
        //given
        Long shopId = 1L;

        repository.save(shopId, 1L);
        repository.save(shopId, 2L);
        repository.save(shopId, 3L);

        //when
        repository.entry(1L, 1L);

        //then

        assertThrows(NotFoundCustomException.class,
            () -> repository.findRank(1L, 1L));
        assertThat(repository.findRank(1L, 2L))
            .isEqualTo(1);
        assertThat(repository.findRank(1L, 3L))
            .isEqualTo(2);
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
        repository.printWaitingLine(1L);
        repository.save(shopId, 1L);
        repository.save(shopId, 2L);
        repository.save(shopId, 3L);
        //when
        repository.cancel(1L, 1L);
        //then
        assertThrows(NotFoundCustomException.class,
            () -> repository.findRank(1L, 1L));
        assertThat(repository.findRank(1L, 2L))
            .isEqualTo(1);
        assertThat(repository.findRank(1L, 3L))
            .isEqualTo(2);
    }
}