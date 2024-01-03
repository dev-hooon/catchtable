package com.prgrms.catchtable.reservation.service;

import static com.prgrms.catchtable.common.exception.ErrorCode.ALREADY_OCCUPIED_RESERVATION_TIME;
import static com.prgrms.catchtable.common.exception.ErrorCode.ALREADY_PREOCCUPIED_RESERVATION_TIME;
import static com.prgrms.catchtable.common.exception.ErrorCode.NOT_EXIST_TIME;
import static com.prgrms.catchtable.reservation.domain.ReservationStatus.COMPLETED;
import static com.prgrms.catchtable.reservation.dto.mapper.ReservationMapper.toCreateReservationResponse;
import static java.lang.Boolean.FALSE;

import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import com.prgrms.catchtable.reservation.domain.Reservation;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.reservation.dto.mapper.ReservationMapper;
import com.prgrms.catchtable.reservation.dto.request.CreateReservationRequest;
import com.prgrms.catchtable.reservation.dto.response.CreateReservationResponse;
import com.prgrms.catchtable.reservation.dto.response.GetAllReservationResponse;
import com.prgrms.catchtable.reservation.repository.ReservationLockRepository;
import com.prgrms.catchtable.reservation.repository.ReservationRepository;
import com.prgrms.catchtable.reservation.repository.ReservationTimeRepository;
import com.prgrms.catchtable.shop.domain.Shop;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationAsync reservationAsync;
    private final ReservationLockRepository reservationLockRepository;

    @Transactional
    public CreateReservationResponse preOccupyReservation(CreateReservationRequest request) {
        Long reservationTimeId = request.reservationTimeId();
        while (FALSE.equals(reservationLockRepository.lock(reservationTimeId))){
            try {
                Thread.sleep(1_500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        ReservationTime reservationTime = reservationTimeRepository.findById(reservationTimeId)
            .orElseThrow(() -> {
                    reservationLockRepository.unlock(reservationTimeId);
                    return new NotFoundCustomException(NOT_EXIST_TIME);
                }
            );

        if (reservationTime.isPreOccupied()) {
            reservationLockRepository.unlock(reservationTimeId);
            throw new BadRequestCustomException(ALREADY_PREOCCUPIED_RESERVATION_TIME);
        }

        reservationAsync.setPreOcuppied(reservationTime);

        Shop shop = reservationTime.getShop();
        reservationLockRepository.unlock(reservationTimeId);

        return CreateReservationResponse.builder()
            .shopName(shop.getName())
            .memberName("memberA")
            .date(reservationTime.getTime())
            .peopleCount(request.peopleCount())
            .build();
    }

    @Transactional
    public CreateReservationResponse registerReservation(CreateReservationRequest request) {
        ReservationTime reservationTime = reservationTimeRepository.findByIdWithShop(
                request.reservationTimeId()).
            orElseThrow(() -> new NotFoundCustomException(NOT_EXIST_TIME));

        if (reservationTime.isOccupied()) {
            throw new BadRequestCustomException(ALREADY_OCCUPIED_RESERVATION_TIME);
        }

        reservationTime.reverseOccupied();

        Reservation reservation = Reservation.builder()
            .status(COMPLETED)
            .peopleCount(request.peopleCount())
            .reservationTime(reservationTime)
            .build();
        Reservation savedReservation = reservationRepository.save(reservation);
        return toCreateReservationResponse(savedReservation);
    }

    @Transactional(readOnly = true)
    public List<GetAllReservationResponse> getAllReservation() {
        List<Reservation> reservations = reservationRepository.findAllWithReservationTimeAndShop();
        return reservations.stream()
            .map(ReservationMapper::toGetAllReservationRepsonse)
            .toList();
    }
}
