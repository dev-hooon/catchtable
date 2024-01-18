package com.prgrms.catchtable.member.controller;

import com.prgrms.catchtable.common.login.LogIn;
import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.member.service.MemberService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/login/kakao")
    public ResponseEntity<?> loginRedirect() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/oauth2/authorization/kakao"));
        return new ResponseEntity<>(httpHeaders, HttpStatus.MOVED_PERMANENTLY);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@LogIn Member member) {
        memberService.logout(member.getEmail());
        return ResponseEntity.ok("logout");
    }

}
