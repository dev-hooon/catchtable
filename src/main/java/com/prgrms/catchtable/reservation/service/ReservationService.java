package com.prgrms.catchtable.reservation.service;

import com.prgrms.catchtable.reservation.dto.request.CreateResercationRequest;
import com.prgrms.catchtable.reservation.dto.response.CreateReservationResponse;
import com.prgrms.catchtable.reservation.repository.ReservationRepository;
import com.prgrms.catchtable.shop.domain.Shop;
import com.prgrms.catchtable.shop.repository.ShopRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ShopRepository shopRepository;
    @Transactional
    public CreateReservationResponse createReservation(Long shopId, CreateResercationRequest request) {
        LocalDateTime requestedReservationTime = request.date();
        int requestedPeopleCount = request.peopleCount();

        Shop shop = shopRepository.findById(shopId).orElseThrow();
        /**
         * 해당 shop의 예약하려는 날짜와 시간이 비어있는 지 확인하는 로직
         * reservationTime = select rt from Shop s join ReservationTime rt where rt.time = :time;
         * reservationTime.isOccupied == true? -> 이미 예약되어있다는 예외 발생
         * reservationTime.isPreOccupied == true? -> 타인이 예약중이라는 예외 발생
         * 선점권 스케줄러 실행
         */

        // 퍼사드를 따로 빼서 이 프로세스가 끝나면 비동기 이벤트가 수행되게 해보자
        // 퍼사드에서 이 서비스 실행 로직 , 그리고 이벤트 발행
        return new CreateReservationResponse(shop.getName(), "member", requestedReservationTime, requestedPeopleCount);
    }
}
