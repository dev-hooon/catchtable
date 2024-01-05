package com.prgrms.catchtable.owner.fixture;

import static com.prgrms.catchtable.member.domain.Gender.*;

import com.prgrms.catchtable.common.data.shop.ShopData;
import com.prgrms.catchtable.member.domain.Gender;
import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.shop.domain.Shop;
import java.time.LocalDate;
import org.springframework.test.util.ReflectionTestUtils;

public class OwnerFixture {

    public static Owner getOwner(){
        Owner owner = Owner.builder()
            .name("ownerA")
            .phoneNumber("010-3462-2480")
            .gender(MALE)
            .dateBirth(LocalDate.of(2000, 9, 13))
            .build();
        Shop shop = ShopData.getShop();
        ReflectionTestUtils.setField(shop, "id", 1L);
        owner.insertShop(shop);
        return owner;
    }

}
