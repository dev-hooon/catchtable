package com.prgrms.catchtable.reservation.service;

import static com.prgrms.catchtable.common.exception.ErrorCode.ALREADY_OCCUPIED_RESERVATION_TIME;
import static com.prgrms.catchtable.common.exception.ErrorCode.ALREADY_PREOCCUPIED_RESERVATION_TIME;
import static com.prgrms.catchtable.common.exception.ErrorCode.NOT_EXIST_TIME;
import static com.prgrms.catchtable.reservation.domain.ReservationStatus.COMPLETED;

import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import com.prgrms.catchtable.reservation.domain.Reservation;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.reservation.dto.request.CreateReservationRequest;
import com.prgrms.catchtable.reservation.repository.ReservationRepository;
import com.prgrms.catchtable.reservation.repository.ReservationTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public ReservationTime validateReservationAndSave(CreateReservationRequest request) {
        ReservationTime reservationTime = reservationTimeRepository.findById(
                request.reservationTimeId())
            .orElseThrow(() -> new NotFoundCustomException(NOT_EXIST_TIME));

        if (reservationTime.isPreOccupied()) {
            throw new BadRequestCustomException(ALREADY_PREOCCUPIED_RESERVATION_TIME);
        }

        return reservationTime;
    }
    
    @Transactional
    public Reservation validateReservationAndSaveIsEmpty(CreateReservationRequest request){
        ReservationTime reservationTime = reservationTimeRepository.findByIdWithShop(
            request.reservationTimeId()).
            orElseThrow(() -> new NotFoundCustomException(NOT_EXIST_TIME));
        
        if(reservationTime.isOccupied()){
            throw new BadRequestCustomException(ALREADY_OCCUPIED_RESERVATION_TIME);
        }

        reservationTime.reverseOccupied();

        Reservation reservation = Reservation.builder()
            .status(COMPLETED)
            .peopleCount(request.peopleCount())
            .shop(reservationTime.getShop())
            .reservationTime(reservationTime)
            .build();
        Reservation savedReservation = reservationRepository.save(reservation);
        return savedReservation;
    }
}
