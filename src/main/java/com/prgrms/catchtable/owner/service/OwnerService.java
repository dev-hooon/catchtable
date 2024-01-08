package com.prgrms.catchtable.owner.service;

import static com.prgrms.catchtable.common.exception.ErrorCode.ALREADY_EXIST_OWNER;
import static com.prgrms.catchtable.common.exception.ErrorCode.INVALID_EMAIL_OR_PASSWORD;

import com.prgrms.catchtable.common.Role;
import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.jwt.provider.JwtTokenProvider;
import com.prgrms.catchtable.jwt.service.RefreshTokenService;
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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public JoinOwnerResponse joinOwner(JoinOwnerRequest joinOwnerRequest) {

        //이미 존재하는 이메일이라면
        validateExistsOwner(joinOwnerRequest);

        String encodePassword = passwordEncoder.encode(joinOwnerRequest.password());

        Gender gender = Gender.of(joinOwnerRequest.gender());

        Owner joinOwner = ownerRepository.save(
            OwnerMapper.toEntity(joinOwnerRequest, encodePassword, gender));

        return OwnerMapper.from(joinOwner);

    }

    private void validateExistsOwner(JoinOwnerRequest joinOwnerRequest) {
        if (ownerRepository.existsOwnerByEmail(joinOwnerRequest.email())) {
            throw new BadRequestCustomException(ALREADY_EXIST_OWNER);
        }
    }

    @Transactional
    public Token loginOwner(LoginOwnerRequest loginRequest) {

        //email 확인
        Owner loginOwner = ownerRepository.findOwnerByEmail(loginRequest.email())
            .orElseThrow(() -> new BadRequestCustomException(INVALID_EMAIL_OR_PASSWORD));

        //password 확인
        validatePassword(loginRequest, loginOwner);

        return createTotalToken(loginOwner.getEmail());
    }

    private void validatePassword(LoginOwnerRequest loginRequest, Owner loginOwner) {
        if (!passwordEncoder.matches(loginRequest.password(), loginOwner.getPassword())) {
            throw new BadRequestCustomException(INVALID_EMAIL_OR_PASSWORD);
        }
    }

    private Token createTotalToken(String email) {
        Token totalToken = jwtTokenProvider.createToken(email, Role.OWNER);
        refreshTokenService.saveRefreshToken(totalToken);
        return totalToken;
    }

}
