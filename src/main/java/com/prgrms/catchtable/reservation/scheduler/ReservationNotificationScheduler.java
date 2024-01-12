package com.prgrms.catchtable.reservation.scheduler;

import static com.prgrms.catchtable.common.notification.NotificationContent.RESERVATION_ONE_HOUR_LEFT;
import static com.prgrms.catchtable.common.notification.NotificationContent.RESERVATION_TIME_OUT;
import static java.time.temporal.ChronoUnit.MINUTES;

import com.prgrms.catchtable.common.notification.NotificationContent;
import com.prgrms.catchtable.notification.dto.request.SendMessageToMemberRequest;
import com.prgrms.catchtable.reservation.domain.Reservation;
import com.prgrms.catchtable.reservation.repository.ReservationRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationNotificationScheduler {
    private final ReservationRepository reservationRepository;
    private final ApplicationEventPublisher publisher;

    private List<Reservation> reservations = new ArrayList<>();

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 당일의 예약 다 가져옴
    public void getAllTodayReservation(){

        LocalDate today = LocalDate.now();

        LocalDateTime startOfDay = today.atStartOfDay(); // 하루의 시작 (오늘의 00시 00분)
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay(); // 하루의 끝 (내일의 00시 00분)

        reservations = reservationRepository.findAllTodayReservation(startOfDay, endOfDay);
        reservations.forEach(reservation -> reservation.getReservationTime().getTime().truncatedTo(MINUTES)); //예약시간의 '초' 다 버림
    }

    @Scheduled(cron = "0 * * * * *") // 1분마다 예약 시간 체크 (1시간 전인 지, 입장시간 됐는 지)
    public void sendReservationMessage(){

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowTime = now.truncatedTo(MINUTES); // '분'까지만 가져옴 (초 버림)

        for (Reservation r : reservations) { // 당일의 예약 리스트를 돌며 시간 만족하는 예약은 알림 발송

            LocalDateTime reservationTime = r.getReservationTime().getTime();

            if (nowTime.isEqual(reservationTime)) { // 예약시간이 한시간 이하로 남았다면
                sendMessage(r, reservationTime, RESERVATION_ONE_HOUR_LEFT); // 한시간 남았다고 알림 발송
                continue;
            }

            if(nowTime.isEqual(reservationTime)){ // 현재시간이 예약시간이 됐다면
                sendMessage(r, reservationTime, RESERVATION_TIME_OUT);
                reservations.remove(r); //예약 입장 알림 보낸 예약은 순회 리스트에서 삭제
            }
        }
    }

    private void sendMessage(Reservation r, LocalDateTime reservationTime, NotificationContent content) {
        SendMessageToMemberRequest request = new SendMessageToMemberRequest(
            r.getMember(),
            content.getMessage(reservationTime.toString())
        );
        publisher.publishEvent(request);
    }
}
