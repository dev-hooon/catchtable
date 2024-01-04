package com.prgrms.catchtable.owner.repository;

import com.prgrms.catchtable.owner.domain.Owner;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

    boolean existsOwnerByEmail(String email);

    Optional<Owner> findOwnerByEmail(String email);

}
