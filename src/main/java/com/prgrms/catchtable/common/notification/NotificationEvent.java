package com.prgrms.catchtable.common.notification;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import com.prgrms.catchtable.notification.dto.request.SendMessageToMemberRequest;
import com.prgrms.catchtable.notification.dto.request.SendMessageToOwnerRequest;
import com.prgrms.catchtable.notification.service.NotificationService;
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
        notificationService.sendMessageAndSave(request.member(), request.content());
    }

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT) // 호출한쪽의 트랜잭션이 커밋 된 후 이벤트 발생
    public void sendMessage(SendMessageToOwnerRequest request) {
        notificationService.sendMessageAndSave(request.owner(), request.content());
    }
}
