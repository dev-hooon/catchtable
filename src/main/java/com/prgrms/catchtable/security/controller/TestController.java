package com.prgrms.catchtable.security.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

    @GetMapping("/testMember")
    public ResponseEntity<String> testMember() {
        log.info("testMember");
        return ResponseEntity.ok("testMember OK");
    }
}
