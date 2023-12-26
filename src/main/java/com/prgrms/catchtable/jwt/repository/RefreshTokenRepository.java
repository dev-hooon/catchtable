package com.prgrms.catchtable.jwt.repository;

import com.prgrms.catchtable.jwt.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
