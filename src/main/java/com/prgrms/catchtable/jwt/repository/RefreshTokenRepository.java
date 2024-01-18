package com.prgrms.catchtable.jwt.repository;

import com.prgrms.catchtable.jwt.domain.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    boolean existsRefreshTokenByEmail(String email);

    void deleteRefreshTokenByEmail(String email);

    Optional<RefreshToken> findRefreshTokenByToken(String token);
}
