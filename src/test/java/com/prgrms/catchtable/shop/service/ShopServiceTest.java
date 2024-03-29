package com.prgrms.catchtable.shop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.owner.fixture.OwnerFixture;
import com.prgrms.catchtable.owner.repository.OwnerRepository;
import com.prgrms.catchtable.reservation.repository.ReservationTimeRepository;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.dto.request.RegisterShopRequest;
import com.prgrms.catchtable.shop.dto.response.GetAllShopResponses;
import com.prgrms.catchtable.shop.dto.response.RegisterShopResponse;
import com.prgrms.catchtable.shop.fixture.ShopFixture;
import com.prgrms.catchtable.shop.repository.ShopRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShopServiceTest {

    @Mock
    private ShopRepository shopRepository;
    @Mock
    private ReservationTimeRepository reservationTimeRepository;
    @Mock
    private OwnerRepository ownerRepository;
    @InjectMocks
    private MemberShopService memberShopService;
    @InjectMocks
    private OwnerShopService ownerShopService;

    @Test
    @DisplayName("Owner가 Shop을 등록한다")
    void registShopTest() {
        //given
        Owner owner = OwnerFixture.getOwner();

        Shop shop = ShopFixture.shop();
        RegisterShopRequest shopRequest = ShopFixture.getRequestDto(shop);

        //when
        when(shopRepository.save(any(Shop.class))).thenReturn(shop);
        RegisterShopResponse registShopResponse = ownerShopService.registerShop(shopRequest, owner);

        //then
        assertThat(registShopResponse.name()).isEqualTo(shop.getName());
        assertThat(registShopResponse.city()).isEqualTo(shop.getAddress().getCity());
        assertThat(registShopResponse.rating()).isEqualTo(shop.getRating());
        assertThat(registShopResponse.openingTime()).isEqualTo(shop.getOpeningTime());
    }

    @Test
    @DisplayName("Shop을 전체 조회할 수 있다.")
    void getAllTest() {
        //given
        Shop shop = ShopFixture.shop();
        Shop shop2 = ShopFixture.shopWith24();
        List<Shop> allShop = List.of(shop, shop2);

        //when
        when(shopRepository.findAll()).thenReturn(allShop);
        GetAllShopResponses all = memberShopService.getAll();

        //then
        assertThat(all.shopResponses().size()).isEqualTo(allShop.size());
    }

    @Test
    @DisplayName("Shop을 단일 조회할 수 있다.")
    void getByIdTest() {
        //given
        Shop shop = ShopFixture.shop();

        //when
        when(shopRepository.findShopById(1L)).thenReturn(Optional.of(shop));
        when(shopRepository.findShopById(2L)).thenReturn(Optional.empty());

        //then
        assertThat(memberShopService.getById(1L).name()).isEqualTo(shop.getName());
        assertThatThrownBy(() -> memberShopService.getById(2L)).isInstanceOf(
            NotFoundCustomException.class);
    }
}