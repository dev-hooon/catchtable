package com.prgrms.catchtable.common.notification;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.notification.dto.request.SendMessageToMemberRequest;
import com.prgrms.catchtable.notification.dto.request.SendMessageToOwnerRequest;
import com.prgrms.catchtable.notification.service.NotificationService;
import com.prgrms.catchtable.owner.domain.Owner;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEvent {

    private final NotificationService notificationService;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT) // 호출한쪽의 트랜잭션이 커밋 된 후 이벤트 발생
    public void sendMessage(SendMessageToMemberRequest request) {
        Member member = request.member();
        if (member.isNotification_activated()) {
            notificationService.sendMessageAndSave(member, request.content());
        }
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT) // 호출한쪽의 트랜잭션이 커밋 된 후 이벤트 발생
    public void sendMessage(SendMessageToOwnerRequest request) {
        Owner owner = request.owner();
        if (owner.isNotification_activated()) {
            notificationService.sendMessageAndSave(request.owner(), request.content());
        }
    }
}
