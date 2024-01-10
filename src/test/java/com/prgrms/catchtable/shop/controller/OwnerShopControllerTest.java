package com.prgrms.catchtable.shop.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.prgrms.catchtable.common.Role;
import com.prgrms.catchtable.common.base.BaseIntegrationTest;
import com.prgrms.catchtable.jwt.provider.JwtTokenProvider;
import com.prgrms.catchtable.jwt.service.RefreshTokenService;
import com.prgrms.catchtable.jwt.token.Token;
import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.owner.fixture.OwnerFixture;
import com.prgrms.catchtable.owner.repository.OwnerRepository;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.dto.request.RegistShopRequest;
import com.prgrms.catchtable.shop.fixture.ShopFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class OwnerShopControllerTest extends BaseIntegrationTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private RefreshTokenService refreshTokenService;

    private Owner owner;
    private String email = "abc1234@gmail.com";
    private Token token;

    @BeforeEach
    void init() {
        //Owner 객체 저장
        owner = OwnerFixture.getOwner(email, "test");
        ownerRepository.save(owner);

        //토큰 발급
        token = jwtTokenProvider.createToken(email, Role.OWNER);
        refreshTokenService.saveRefreshToken(token);

        //토큰 헤더 세팅
        httpHeaders.add("AccessToken", token.getAccessToken());
        httpHeaders.add("RefreshToken", token.getRefreshToken());
    }

    @Test
    @DisplayName("Owner가 Shop을 등록한다.")
    void registShopTest() throws Exception {
        //given
        Shop shop = ShopFixture.shop();
        RegistShopRequest shopRequest = ShopFixture.getRequestDto(shop);

        //then
        mockMvc.perform(post("/owners/shops")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders)
                .content(asJsonString(shopRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value(shopRequest.name()))
            .andExpect(jsonPath("$.rating").value(shopRequest.rating()))
            .andExpect(jsonPath("$.category").value(shopRequest.category()))
            .andExpect(jsonPath("$.city").value(shopRequest.city()))
            .andExpect(jsonPath("$.district").value(shopRequest.district()))
            .andExpect(jsonPath("$.capacity").value(shopRequest.capacity()));
    }
}