package com.prgrms.catchtable.jwt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    private String clientSecret;
    private int expiryMinute;
    private int expiryMinuteRefresh;

}
