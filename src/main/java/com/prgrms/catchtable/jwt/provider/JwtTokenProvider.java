package com.prgrms.catchtable.jwt.provider;


import com.prgrms.catchtable.common.Role;
import com.prgrms.catchtable.jwt.config.JwtConfig;
import com.prgrms.catchtable.jwt.service.JwtUserDetailsService;
import com.prgrms.catchtable.jwt.token.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtConfig jwtConfig;
    private final JwtUserDetailsService jwtUserDetailsService;
    private final String JWT_ROLE= "ROLE";

    public Token createToken(String email, Role role) {

        Claims claims = Jwts.claims().setSubject(email);
        claims.put(JWT_ROLE, role);
        Date now = new Date();

        String accessToken = createAccessToken(claims, now);
        String refreshToken = createRefreshToken(claims, now);

        return new Token(accessToken, refreshToken, email, role);
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

    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtConfig.getClientSecret())
                .build()
                .parseClaimsJws(token)
                .getBody();

            return claims.getExpiration().after(new Date());
        } catch (JwtException je) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        String email = getEmail(token);
        Role role = getRole(token);

        UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(email, role);
        return new UsernamePasswordAuthenticationToken(userDetails, "",
            userDetails.getAuthorities());
    }

    private String getEmail(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(jwtConfig.getClientSecret())
            .build()
            .parseClaimsJws(token)
            .getBody();

        return claims.getSubject();
    }

    private Role getRole(String token){
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(jwtConfig.getClientSecret())
            .build()
            .parseClaimsJws(token)
            .getBody();

        return (Role) claims.get(JWT_ROLE);
    }
}
