package com.prgrms.catchtable.notification.repository;

import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.notification.domain.NotificationMember;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationMemberRepository extends JpaRepository<NotificationMember, Long> {

    Optional<NotificationMember> findByMember(Member member);
}
