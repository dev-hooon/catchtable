package com.prgrms.catchtable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CatchtableApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatchtableApplication.class, args);
    }

}
