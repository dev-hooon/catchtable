package com.prgrms.catchtable.security.config;

import com.prgrms.catchtable.jwt.filter.JwtAuthenticationFilter;
import com.prgrms.catchtable.security.filter.ExceptionHandlerFilter;
import com.prgrms.catchtable.security.service.CustomOAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2SuccessHandler successHandler;
    private final ExceptionHandlerFilter exceptionHandlerFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS))
            .formLogin(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorization -> authorization
                .requestMatchers(new AntPathRequestMatcher("/testMember/**")).hasRole("MEMBER")
                .requestMatchers(new AntPathRequestMatcher("/**")).permitAll()
            )
            .oauth2Login(oauth2Login -> oauth2Login.successHandler(successHandler))
            .exceptionHandling(exhandle -> exhandle
                .authenticationEntryPoint(((request, response, authException) ->
                    response.sendError(401)))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                    response.sendError(403)));

        http.addFilterBefore(exceptionHandlerFilter,
            OAuth2AuthorizationRequestRedirectFilter.class);

        http.addFilterBefore(jwtAuthenticationFilter,
            OAuth2AuthorizationRequestRedirectFilter.class);

        return http.build();
    }

}
