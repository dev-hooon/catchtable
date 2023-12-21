package com.prgrms.catchtable.notification.repository;

import com.prgrms.catchtable.notification.domain.NotificationMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationMemberRepository extends JpaRepository<NotificationMember, Long> {

}
