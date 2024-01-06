package com.prgrms.catchtable.reservation.service;


import static com.prgrms.catchtable.common.exception.ErrorCode.ALREADY_COMPLETED;
import static com.prgrms.catchtable.common.exception.ErrorCode.NOT_EXIST_RESERVATION;
import static com.prgrms.catchtable.reservation.domain.ReservationStatus.COMPLETED;

import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.owner.repository.OwnerRepository;
import com.prgrms.catchtable.reservation.domain.Reservation;
import com.prgrms.catchtable.reservation.domain.ReservationStatus;
import com.prgrms.catchtable.reservation.dto.mapper.ReservationMapper;
import com.prgrms.catchtable.reservation.dto.request.ModifyReservationStatusRequest;
import com.prgrms.catchtable.reservation.dto.response.OwnerGetAllReservationResponse;
import com.prgrms.catchtable.reservation.repository.ReservationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerReservationService {

    private final ReservationRepository reservationRepository;
    private final OwnerRepository ownerRepository;

    /**
     * 예약 취소, 노쇼 처리
     *
     * @param reservationId
     * @param request
     */
    @Transactional
    public void modifyReservationStatus(
        Long reservationId,
        ModifyReservationStatusRequest request
    ) {
        ReservationStatus modifyStatus = request.status(); // 요청으로 들어온 변경하려는 예약상태 추출

        if(modifyStatus == COMPLETED){ // 취소, 노쇼 처리가 아닌 경우 예외
            throw new BadRequestCustomException(ALREADY_COMPLETED);
        }

        Reservation reservation = reservationRepository
            .findByIdWithReservationTimeAndShop(reservationId)
            .orElseThrow(() -> new NotFoundCustomException(NOT_EXIST_RESERVATION));

        reservation.changeStatus(modifyStatus); // 해당 예약의 상태를 요청으로 들어온 상태로 변경

        reservation.getReservationTime().reverseOccupied(); // 해당 예약의 예약시간을 빈 상태로 변경
    }

    /**
     * owner가 자신의 가게에 등록된 예약 전체 조회
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<OwnerGetAllReservationResponse> getAllReservation(Long ownerId) {
        Owner owner = ownerRepository.findById(ownerId).orElseThrow();
        List<Reservation> reservations = reservationRepository.findAllWithReservationTimeAndShopByShopId(
            owner.getShop().getId());

        return reservations.stream()
            .map(ReservationMapper::toOwnerGetAllReservationResponse).toList();
    }
}
