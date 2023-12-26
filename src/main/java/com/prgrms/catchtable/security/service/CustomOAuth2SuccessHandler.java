package com.prgrms.catchtable.security.service;

import com.prgrms.catchtable.member.service.MemberService;
import com.prgrms.catchtable.security.dto.OAuthAttribute;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final MemberService memberService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {

        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2User oauth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();
            String provider = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

            OAuthAttribute oAuthAttribute = OAuthAttribute.of(oauth2User, provider);

            //JWT 토큰 함께 발급
            memberService.oauthLogin(oAuthAttribute);

            //JWT 토큰을 Json으로 전달

        }
    }
}
