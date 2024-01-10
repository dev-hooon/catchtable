package com.prgrms.catchtable.notification.dto.request;

import com.prgrms.catchtable.common.notification.NotificationContent;
import com.prgrms.catchtable.member.domain.Member;
import lombok.Builder;

@Builder
public record SendMessageToMemberRequest(Member member,
                                         String content) {

}
