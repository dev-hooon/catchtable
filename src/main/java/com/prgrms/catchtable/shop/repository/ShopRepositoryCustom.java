package com.prgrms.catchtable.shop.repository;

import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.dto.request.ShopSearchCondition;
import java.util.List;

public interface ShopRepositoryCustom {

    List<Shop> search(ShopSearchCondition condition);

}
