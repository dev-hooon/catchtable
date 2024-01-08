package com.prgrms.catchtable.owner.fixture;

import static com.prgrms.catchtable.member.domain.Gender.MALE;

import com.prgrms.catchtable.common.data.shop.ShopData;
import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.owner.dto.request.JoinOwnerRequest;
import com.prgrms.catchtable.owner.dto.request.LoginOwnerRequest;
import com.prgrms.catchtable.shop.domain.Shop;
import java.time.LocalDate;
import org.springframework.test.util.ReflectionTestUtils;


public class OwnerFixture {

    public static Owner getOwner(String email, String password) {
        Owner owner = Owner.builder()
            .name("ownerA")
            .email(email)
            .password(password)
            .phoneNumber("010-3462-2480")
            .gender(MALE)
            .dateBirth(LocalDate.of(2000, 9, 13))
            .build();
        Shop shop = ShopData.getShop();
        ReflectionTestUtils.setField(shop, "id", 1L);
        owner.insertShop(shop);
        return owner;
    }

    public static JoinOwnerRequest getJoinOwnerRequest(String email, String password) {
        return JoinOwnerRequest.builder()
            .name("ownerA")
            .email(email)
            .password(password)
            .phoneNumber("010-3462-2480")
            .gender("male")
            .dateBirth(LocalDate.of(2000, 9, 13))
            .build();
    }

    public static LoginOwnerRequest getLoginOwnerRequest(String email, String password) {
        return LoginOwnerRequest.builder()
            .email(email)
            .password(password)
            .build();
    }
}
