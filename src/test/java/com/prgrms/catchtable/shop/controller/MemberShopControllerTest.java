package com.prgrms.catchtable.shop.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.prgrms.catchtable.common.Role;
import com.prgrms.catchtable.common.base.BaseIntegrationTest;
import com.prgrms.catchtable.jwt.provider.JwtTokenProvider;
import com.prgrms.catchtable.jwt.service.RefreshTokenService;
import com.prgrms.catchtable.jwt.token.Token;
import com.prgrms.catchtable.member.MemberFixture;
import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.member.repository.MemberRepository;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.dto.request.ShopSearchCondition;
import com.prgrms.catchtable.shop.fixture.ShopFixture;
import com.prgrms.catchtable.shop.repository.ShopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Transactional
class MemberShopControllerTest extends BaseIntegrationTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private RefreshTokenService refreshTokenService;

    private Member member;
    private Shop shop;
    private String email = "abc1234@gmail.com";
    private Token token;

    @BeforeEach
    void init() {
        //Member 객체 저장
        member = MemberFixture.member(email);
        memberRepository.save(member);

        //Shop 객체 저장
        shop = shopRepository.save(ShopFixture.shop());

        //토큰 발급
        token = jwtTokenProvider.createToken(email, Role.MEMBER);
        refreshTokenService.saveRefreshToken(token);

        //토큰 헤더 세팅
        httpHeaders.add("AccessToken", token.getAccessToken());
        httpHeaders.add("RefreshToken", token.getRefreshToken());
    }

    @Test
    @DisplayName("Member가 Shop을 전체조회 한다.")
    void getAllTest() throws Exception {
        //then
        mockMvc.perform(get("/shops")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.shopResponses.[0].name").value(shop.getName()))
            .andExpect(jsonPath("$.shopResponses.[0].rating").value(shop.getRating()))
            .andExpect(jsonPath("$.shopResponses.[0].category").value(shop.getCategory().getType()))
            .andExpect(jsonPath("$.shopResponses.[0].city").value(shop.getAddress().getCity()))
            .andExpect(jsonPath("$.shopResponses.[0].district").value(shop.getAddress().getDistrict()))
            .andExpect(jsonPath("$.shopResponses.[0].capacity").value(shop.getCapacity()));
    }

    @Test
    @DisplayName("Member가 Shop을 단일조회 한다.")
    void getByIdTest() throws Exception {
        //given
        Shop shop1 = shopRepository.findAll().get(0);

        //then
        mockMvc.perform(get("/shops/{shopId}", shop1.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .headers(httpHeaders))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(shop.getName()))
            .andExpect(jsonPath("$.rating").value(shop.getRating()))
            .andExpect(jsonPath("$.category").value(shop.getCategory().getType()))
            .andExpect(jsonPath("$.city").value(shop.getAddress().getCity()))
            .andExpect(jsonPath("$.district").value(shop.getAddress().getDistrict()))
            .andExpect(jsonPath("$.capacity").value(shop.getCapacity()));
    }

    @Test
    @DisplayName("Member가 Shop을 필터 검색 조회 한다.")
    void getBySearchTest() throws Exception {
        //given
        Shop shop1 = shopRepository.findAll().get(0);
        ShopSearchCondition shopSearchCondition = new ShopSearchCondition(shop1.getName(), shop1.getCategory().getType(), shop1.getAddress().getCity());
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        //when
        params.add("name", shopSearchCondition.name());
        params.add("category", shopSearchCondition.category());
        params.add("city", shopSearchCondition.city());

        //then
        mockMvc.perform(get("/shops/search")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders)
                .params(params))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.shopResponses.[0].name").value(shop.getName()))
            .andExpect(jsonPath("$.shopResponses.[0].rating").value(shop.getRating()))
            .andExpect(jsonPath("$.shopResponses.[0].category").value(shop.getCategory().getType()))
            .andExpect(jsonPath("$.shopResponses.[0].city").value(shop.getAddress().getCity()))
            .andExpect(jsonPath("$.shopResponses.[0].district").value(shop.getAddress().getDistrict()))
            .andExpect(jsonPath("$.shopResponses.[0].capacity").value(shop.getCapacity()));

    }
}