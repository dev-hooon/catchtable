package com.prgrms.catchtable.waiting.service;

import static com.prgrms.catchtable.common.notification.WaitingNotificationContent.FIRST_RANK;
import static com.prgrms.catchtable.common.notification.WaitingNotificationContent.MEMBER_CANCELED;
import static com.prgrms.catchtable.common.notification.WaitingNotificationContent.MEMBER_COMPLETED;
import static com.prgrms.catchtable.common.notification.WaitingNotificationContent.MEMBER_CREATED;
import static com.prgrms.catchtable.common.notification.WaitingNotificationContent.MEMBER_POSTPONED;
import static com.prgrms.catchtable.common.notification.WaitingNotificationContent.OWNER_CANCELED;
import static com.prgrms.catchtable.common.notification.WaitingNotificationContent.OWNER_CREATED;
import static com.prgrms.catchtable.common.notification.WaitingNotificationContent.THIRD_RANK;

import com.prgrms.catchtable.common.notification.WaitingNotificationContent;
import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.notification.dto.request.SendMessageToMemberRequest;
import com.prgrms.catchtable.notification.dto.request.SendMessageToOwnerRequest;
import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.waiting.repository.WaitingRepository;
import com.prgrms.catchtable.waiting.repository.waitingline.WaitingLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WaitingNotification {

    private final ApplicationEventPublisher publisher;
    private final WaitingLineRepository waitingLineRepository;
    private final WaitingRepository waitingRepository;

    public void sendMessageAsCreated(Member member, Owner owner, Long rank) {
        sendMessageToMember(member, String.format(MEMBER_CREATED.getContent(), rank));
        sendMessageToOwner(owner, String.format(OWNER_CREATED.getContent(), rank));
    }

    public void sendMessageAsPostponed(Member member, Long rank) {
        sendMessageToMember(member, String.format(MEMBER_POSTPONED.getContent(), rank));
    }

    public void sendMessageAsCompleted(Member member) {
        sendMessageToMember(member, MEMBER_COMPLETED.getContent());
    }

    public void sendMessageAsCanceled(Member member, Owner owner, Long rank) {
        sendMessageToMember(member, MEMBER_CANCELED.getContent());
        sendMessageToOwner(owner, String.format(OWNER_CANCELED.getContent(), rank));
    }

    public void sendEntryMessageToOthers(Long shopId, Long removedMemberRank) {
        if (removedMemberRank <= 3) { // 대기열에서 rank 3이하 회원이 사라지면
            sendEntryMessageToOthers(shopId, 3, THIRD_RANK); // 세 번째 회원에게 알림
        }
        if (removedMemberRank == 1) { //대기열에서 rank 1 회원이 사라지면
            sendEntryMessageToOthers(shopId, 1, FIRST_RANK); // 첫 번째 회원에게 알림
        }
    }

    private void sendEntryMessageToOthers(Long shopId, int rank,
        WaitingNotificationContent content) {
        Long waitingId = waitingLineRepository.findRankValue(shopId, rank); // rank로 waitingId 찾기
        if (waitingId != null) {
            Member member = waitingRepository.findWaitingWithMember(waitingId).getMember();
            sendMessageToMember(member, content.getContent());
        }
    }

    private void sendMessageToMember(Member member, String content) {
        SendMessageToMemberRequest request = SendMessageToMemberRequest.builder()
            .member(member)
            .content(content)
            .build();
        publisher.publishEvent(request);
    }

    private void sendMessageToOwner(Owner owner, String content) {
        SendMessageToOwnerRequest request = SendMessageToOwnerRequest.builder()
            .owner(owner)
            .content(content)
            .build();
        publisher.publishEvent(request);
    }
}
