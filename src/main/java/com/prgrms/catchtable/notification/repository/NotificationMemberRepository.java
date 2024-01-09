package com.prgrms.catchtable.notification.repository;

import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.notification.domain.NotificationMember;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationMemberRepository extends JpaRepository<NotificationMember, Long> {

    List<NotificationMember> findByMember(Member member);
}
