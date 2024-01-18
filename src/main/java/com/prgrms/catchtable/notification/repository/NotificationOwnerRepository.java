package com.prgrms.catchtable.notification.repository;

import com.prgrms.catchtable.notification.domain.NotificationOwner;
import com.prgrms.catchtable.owner.domain.Owner;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationOwnerRepository extends JpaRepository<NotificationOwner, Long> {

    List<NotificationOwner> findByOwner(Owner owner);
}
