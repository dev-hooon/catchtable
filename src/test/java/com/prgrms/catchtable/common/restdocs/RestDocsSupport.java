package com.prgrms.catchtable.common.restdocs;

import static com.prgrms.catchtable.common.Role.MEMBER;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.catchtable.jwt.provider.JwtTokenProvider;
import com.prgrms.catchtable.jwt.token.Token;
import com.prgrms.catchtable.member.domain.Member;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.security.config.BeanIds;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;

@ExtendWith(RestDocumentationExtension.class)
@Import(RestDocsConfig.class)
@SpringBootTest
public abstract class RestDocsSupport {

    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected RestDocumentationResultHandler restDocs;
    protected HttpHeaders httpHeaders = new HttpHeaders();

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp(final WebApplicationContext context,
        final RestDocumentationContextProvider provider) throws ServletException {
        DelegatingFilterProxy delegateProxyFilter = new DelegatingFilterProxy();
        delegateProxyFilter.init(
            new MockFilterConfig(context.getServletContext(),
                BeanIds.SPRING_SECURITY_FILTER_CHAIN));

        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(
                MockMvcRestDocumentation.documentationConfiguration(provider))  // rest docs 설정 주입
            .alwaysDo(restDocs)
            .alwaysDo(MockMvcResultHandlers.print())
            .addFilters(
                new CharacterEncodingFilter("UTF-8", true),
                delegateProxyFilter
            )
            .build();
    }

    public HttpHeaders getHttpHeaders(Member member) {
        Token token = jwtTokenProvider.createToken(member.getEmail(), MEMBER);
        httpHeaders.add("AccessToken", token.getAccessToken());
        httpHeaders.add("RefreshToken", token.getRefreshToken());
        return httpHeaders;
    }
}
