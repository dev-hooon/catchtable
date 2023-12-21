package com.prgrms.catchtable.waiting.repository;

import com.prgrms.catchtable.waiting.domain.Waiting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {

}
