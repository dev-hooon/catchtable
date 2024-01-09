package com.prgrms.catchtable.jwt.service;

import static com.prgrms.catchtable.common.exception.ErrorCode.NOT_FOUND_REFRESH_TOKEN;

import com.prgrms.catchtable.common.Role;
import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import com.prgrms.catchtable.jwt.domain.RefreshToken;
import com.prgrms.catchtable.jwt.repository.RefreshTokenRepository;
import com.prgrms.catchtable.jwt.token.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void saveRefreshToken(Token totalToken) {
        String email = totalToken.getEmail();
        Role role = totalToken.getRole();

        if (refreshTokenRepository.existsRefreshTokenByEmail(email)) {
            refreshTokenRepository.deleteRefreshTokenByEmail(email);
        }

        RefreshToken newRefreshToken = RefreshToken.builder()
            .token(totalToken.getRefreshToken())
            .email(email)
            .role(role)
            .build();

        refreshTokenRepository.save(newRefreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken getRefreshTokenByToken(String refreshToken) {
        return refreshTokenRepository.findRefreshTokenByToken(refreshToken)
            .orElseThrow(() -> new NotFoundCustomException(NOT_FOUND_REFRESH_TOKEN));
    }
}
