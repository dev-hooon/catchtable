package com.prgrms.catchtable.common.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public abstract class BaseIntegrationTest {

    @Autowired
    public MockMvc mockMvc;
    public static ObjectMapper objectMapper = new ObjectMapper();

    public static String asJsonString(final Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }
}
