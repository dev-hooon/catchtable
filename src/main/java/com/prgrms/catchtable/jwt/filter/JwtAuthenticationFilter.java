package com.prgrms.catchtable.jwt.filter;

import static com.prgrms.catchtable.common.exception.ErrorCode.TOKEN_EXPIRES;

import com.prgrms.catchtable.common.Role;
import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.jwt.domain.RefreshToken;
import com.prgrms.catchtable.jwt.provider.JwtTokenProvider;
import com.prgrms.catchtable.jwt.service.RefreshTokenService;
import com.prgrms.catchtable.jwt.token.Token;
import jakarta.servlet.FilterChain;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private final RefreshTokenService refreshTokenService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        String accessToken = ((HttpServletRequest) request).getHeader("AccessToken");
        String refreshToken = ((HttpServletRequest) request).getHeader("RefreshToken");

        if (accessToken != null) {
            //AccessToken 유효
            if (jwtTokenProvider.validateToken(accessToken)) {
                setAuthentication(accessToken);
            }
            //AccessToken 유효 X
            else {
                if (refreshToken != null) {
                    //RefreshToken 유효
                    if (jwtTokenProvider.validateToken(refreshToken)) {
                        RefreshToken refreshTokenEntity = refreshTokenService.getRefreshTokenByToken(
                            refreshToken);
                        String email = refreshTokenEntity.getEmail();
                        Role role = refreshTokenEntity.getRole();
                        Token newToken = jwtTokenProvider.createToken(email, role);

                        ((HttpServletResponse) response).setHeader("AccessToken",
                            newToken.getAccessToken());
                        setAuthentication(newToken.getAccessToken());
                    }
                    //RefreshToken 유효 X
                    else {
                        throw new BadRequestCustomException(TOKEN_EXPIRES);
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }

    private void setAuthentication(String accessToken) {
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
