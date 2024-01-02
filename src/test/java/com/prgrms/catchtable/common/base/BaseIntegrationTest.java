package com.prgrms.catchtable.common.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class BaseIntegrationTest {

    public static ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    public MockMvc mockMvc;

    public static String asJsonString(final Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }
}
