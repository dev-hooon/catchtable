package com.prgrms.catchtable.member.repository;

import com.prgrms.catchtable.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
