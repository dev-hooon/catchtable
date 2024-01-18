package com.prgrms.catchtable.owner.repository;

import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.shop.domain.Shop;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

    boolean existsOwnerByEmail(String email);

    Optional<Owner> findOwnerByEmail(String email);

    Optional<Owner> findOwnerByShop(Shop shop);

}
