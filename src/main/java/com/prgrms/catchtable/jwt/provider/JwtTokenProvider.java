package com.prgrms.catchtable.jwt.provider;


import com.prgrms.catchtable.jwt.config.JwtConfig;
import com.prgrms.catchtable.jwt.token.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtConfig jwtConfig;

    public Token createToken(String email) {

        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();

        String accessToken = createAccessToken(claims, now);
        String refreshToken = createRefreshToken(claims, now);

        return new Token(accessToken, refreshToken, email);

    }

    private String createAccessToken(Claims claims, Date now) {
        long expiryMinute = jwtConfig.getExpiryMinute() * 1000L * 60;

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + expiryMinute))
            .signWith(SignatureAlgorithm.HS256, jwtConfig.getClientSecret())
            .compact();
    }

    private String createRefreshToken(Claims claims, Date now) {

        long expiryMinuteRefresh = jwtConfig.getExpiryMinuteRefresh() * 1000L * 60;

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(new Date(now.getTime() + expiryMinuteRefresh))
            .signWith(SignatureAlgorithm.HS256, jwtConfig.getClientSecret())
            .compact();
    }

}
