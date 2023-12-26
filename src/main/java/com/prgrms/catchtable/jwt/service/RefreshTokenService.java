package com.prgrms.catchtable.jwt.service;

import com.prgrms.catchtable.jwt.domain.RefreshToken;
import com.prgrms.catchtable.jwt.repository.RefreshTokenRepository;
import com.prgrms.catchtable.jwt.token.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void saveRefreshToken(Token totalToken){
        String email = totalToken.getEmail();

        if(refreshTokenRepository.existsRefreshTokenByEmail(email)){
            refreshTokenRepository.deleteRefreshTokenByEmail(email);
        }

        RefreshToken newRefreshToken = RefreshToken.builder()
            .token(totalToken.getRefreshToken())
            .email(email)
            .build();

        refreshTokenRepository.save(newRefreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken getRefreshTokenByToken(String refreshToken){
        return refreshTokenRepository.findRefreshTokenByToken(refreshToken)
            .orElseThrow(() -> new UsernameNotFoundException("Not Found RefreshToken"));
    }
}
