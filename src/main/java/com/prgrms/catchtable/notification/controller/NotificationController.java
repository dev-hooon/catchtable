package com.prgrms.catchtable.notification.controller;

import com.prgrms.catchtable.notification.dto.request.SendMessageRequest;
import com.prgrms.catchtable.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/test")
    public void test(@RequestBody SendMessageRequest request){
        notificationService.sendMessageToMemberAndSave(request);
    }
}
