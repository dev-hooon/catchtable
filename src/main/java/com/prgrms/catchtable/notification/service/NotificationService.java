package com.prgrms.catchtable.notification.service;

import static com.prgrms.catchtable.common.exception.ErrorCode.SLACK_ID_IS_WRONG;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.member.repository.MemberRepository;
import com.prgrms.catchtable.notification.domain.NotificationMember;
import com.prgrms.catchtable.notification.domain.NotificationOwner;
import com.prgrms.catchtable.notification.dto.request.SendMessageRequest;
import com.prgrms.catchtable.notification.repository.NotificationMemberRepository;
import com.prgrms.catchtable.notification.repository.NotificationOwnerRepository;
import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.owner.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    @Value("${slack.token}")
    private String slackToken;

    private final NotificationMemberRepository notificationMemberRepository;
    private final NotificationOwnerRepository notificationOwnerRepository;
    private final MemberRepository memberRepository; // 추후 삭제 예정
    private final OwnerRepository ownerRepository; // 추후 삭제 예정
    private JSONObject jsonObject;

    public void sendMessageToMemberAndSave(SendMessageRequest request) {
        String url = "https://slack.com/api/chat.postMessage"; // slack 메세지를 보내도록 요청하는 Slack API
        // member 예제 데이터
        Member member = Member.builder()
            .email("dlswns661035@gmail.com") // 이 부분 이메일 바꿔서 하면 해당 이메일의 슬랙 개인으로 dm 보냄
            .build();

        String email = member.getEmail();
        String message = request.content().getMessage();
        String slackId = getSlackIdByEmail(email); // 이메일을 통해 사용자의 슬랙 고유 ID 추출

        requestToSendMessage(slackId, message); // 알림 요청 보내는 함수 호출

        NotificationMember notification = NotificationMember.builder()
            .member(member)
            .message(message)
            .build();
        memberRepository.save(member); // 추후 삭제 예정
        notificationMemberRepository.save(notification); // 해당 사용자의 알림 생성 후 저장

    }

    public void sendMessageToOwnerAndSave(SendMessageRequest request) {
        String url = "https://slack.com/api/chat.postMessage"; // slack 메세지를 보내도록 요청하는 Slack API
        //Owner 예제 데이터
        Owner owner = Owner.builder()
            .email("dlswns661035@gmail.com") // 이 부분 이메일 바꿔서 하면 해당 이메일의 슬랙 개인으로 dm 보냄
            .build();

        String email = owner.getEmail();
        String message = request.content().getMessage();
        String slackId = getSlackIdByEmail(email);

        requestToSendMessage(slackId, message);

        NotificationOwner notification = NotificationOwner.builder()
            .owner(owner)
            .message(message)
            .build();
        ownerRepository.save(owner); //추후 삭제 예정
        notificationOwnerRepository.save(notification);
    }

    private void requestToSendMessage(String slackId, String message) {
        String url = "https://slack.com/api/chat.postMessage";

        // 헤더에 캐치테이블 클론 슬랙 토큰 삽입
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + slackToken);
        headers.add("Content-type", "application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("channel", slackId); // 채널 필드에 사용자의 슬랙 고유 ID
        jsonObject.put("text", message); // 메세지 필드에 메세지
        String body = jsonObject.toString();

        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(
            url,
            POST,
            requestEntity,
            String.class
        );// post로 위에서 만든 Json body 전송 요청

        jsonObject = new JSONObject(response.getBody());
        String result = jsonObject.get("ok").toString();

        if (result.equals("false")) { // 알림 요청 보낸 후 응답의 ok필드 값이 false면 슬랙아이디가 잘못되었다는 것
            throw new BadRequestCustomException(SLACK_ID_IS_WRONG);
        }

        // ok 필드값이 true면 알림 전송 완료 된것임
    }

    public String getSlackIdByEmail(String email) {
        String url = "https://slack.com/api/users.lookupByEmail?email=".concat(email);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + slackToken);
        headers.add("Content-type", "application/x-www-form-urlencoded");

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
            url,
            GET,
            requestEntity,
            String.class
        );
        jsonObject = new JSONObject(responseEntity.getBody());
        JSONObject profile = jsonObject.getJSONObject("user");

        return profile.get("id").toString();
    }
}
