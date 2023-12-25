package com.prgrms.catchtable.reservation.service;

import static com.prgrms.catchtable.reservation.domain.ReservationStatus.*;

import com.prgrms.catchtable.reservation.domain.Reservation;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.reservation.dto.request.CreateReservationRequest;
import com.prgrms.catchtable.reservation.dto.response.ValidateReservationResponse;
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
    public ValidateReservationResponse validateReservationIsPossible(Long shopId,
        CreateReservationRequest request) {
        LocalDateTime requestedReservationTime = request.date();
        int requestedPeopleCount = request.peopleCount();

        Shop shop = shopRepository.findById(shopId).orElseThrow();
        //예제 데이터
        ReservationTime reservationTime = ReservationTime.builder()
            .time(request.date())
            .build();
        Reservation reservation = Reservation.builder()
            .status(COMPLETED)
            .peopleCount(request.peopleCount())
            .build();

        if (reservationTime.isPreOccupied()) {
            throw new RuntimeException("타인에게 선점권이 있음");
        }
        if (reservationTime.isOccupied()) {
            throw new RuntimeException("이미 예약된 시간임");
        }

        reservation.insertReservvationTime(reservationTime);
        reservation.insertShop(shop);

        Reservation savedReservation = reservationRepository.save(reservation);

        return new ValidateReservationResponse(savedReservation.getShop().getName(), savedReservation.getTime());
    }
}
