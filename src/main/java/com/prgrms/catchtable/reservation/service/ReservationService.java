package com.prgrms.catchtable.reservation.service;

import static com.prgrms.catchtable.common.exception.ErrorCode.IS_PRE_OCCUPIED;
import static com.prgrms.catchtable.common.exception.ErrorCode.NOT_EXIST_TIME;

import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.reservation.dto.request.CreateReservationRequest;
import com.prgrms.catchtable.reservation.repository.ReservationTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationTimeRepository reservationTimeRepository;

    @Transactional
    public ReservationTime validateReservationAndSave(CreateReservationRequest request) {
        ReservationTime reservationTime = reservationTimeRepository.findById(
                request.reservationTimeId())
            .orElseThrow(() -> new NotFoundCustomException(NOT_EXIST_TIME));

        if (reservationTime.isPreOccupied()) {
            throw new BadRequestCustomException(IS_PRE_OCCUPIED);
        }

        return reservationTime;
    }
}
