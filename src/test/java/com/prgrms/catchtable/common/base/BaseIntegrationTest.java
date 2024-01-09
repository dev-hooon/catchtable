package com.prgrms.catchtable.common.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.prgrms.catchtable.jwt.provider.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class BaseIntegrationTest {

    @Autowired
    public JwtTokenProvider jwtTokenProvider;

    public static ObjectMapper objectMapper = new ObjectMapper();

    public HttpHeaders httpHeaders = new HttpHeaders();
    @Autowired
    public MockMvc mockMvc;

    public static String asJsonString(final Object object) throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(object);
    }
}
