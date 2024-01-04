package com.prgrms.catchtable.owner.service;

import static com.prgrms.catchtable.common.exception.ErrorCode.*;

import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.jwt.provider.JwtTokenProvider;
import com.prgrms.catchtable.jwt.token.Token;
import com.prgrms.catchtable.member.domain.Gender;
import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.owner.dto.OwnerMapper;
import com.prgrms.catchtable.owner.dto.request.JoinOwnerRequest;
import com.prgrms.catchtable.owner.dto.request.LoginOwnerRequest;
import com.prgrms.catchtable.owner.dto.response.JoinOwnerResponse;
import com.prgrms.catchtable.owner.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public JoinOwnerResponse joinOwner(JoinOwnerRequest joinOwnerRequest) {

        //이미 존재하는 이메일이라면
        if (ownerRepository.existsOwnerByEmail(joinOwnerRequest.email())) {
            throw new BadRequestCustomException(ALREADY_EXIST_OWNER);
        }

        String encodePassword = passwordEncoder.encode(joinOwnerRequest.password());

        Gender gender = Gender.of(joinOwnerRequest.gender());

        Owner joinOwner = ownerRepository.save(OwnerMapper.toEntity(joinOwnerRequest, encodePassword, gender));

        return OwnerMapper.from(joinOwner);

    }

    public Token loginOwner(LoginOwnerRequest loginRequest){

        //email 확인
        Owner loginOwner = ownerRepository.findOwnerByEmail(loginRequest.email())
            .orElseThrow(() -> new BadRequestCustomException(BAD_REQUEST_EMAIL_OR_PASSWORD));

        //password 확인
        if(passwordEncoder.matches(loginRequest.password(), loginOwner.getPassword())){
            throw new BadRequestCustomException(BAD_REQUEST_EMAIL_OR_PASSWORD);
        }

        return jwtTokenProvider.createToken(loginOwner.getEmail());
    }

}
