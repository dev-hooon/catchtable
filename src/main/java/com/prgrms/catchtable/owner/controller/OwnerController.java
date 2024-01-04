package com.prgrms.catchtable.owner.controller;

import com.prgrms.catchtable.jwt.token.Token;
import com.prgrms.catchtable.owner.dto.request.JoinOwnerRequest;
import com.prgrms.catchtable.owner.dto.request.LoginOwnerRequest;
import com.prgrms.catchtable.owner.dto.response.JoinOwnerResponse;
import com.prgrms.catchtable.owner.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/owners")
public class OwnerController {

    private final OwnerService ownerService;

    @PostMapping("/join")
    public ResponseEntity<JoinOwnerResponse> join(@Valid @RequestBody JoinOwnerRequest joinOwnerRequest){
        JoinOwnerResponse joinOwnerResponse = ownerService.joinOwner(joinOwnerRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(joinOwnerResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<Token> login(@Valid @RequestBody LoginOwnerRequest loginOwnerRequest){
        Token responseToken = ownerService.loginOwner(loginOwnerRequest);

        return ResponseEntity.ok(responseToken);
    }

}
