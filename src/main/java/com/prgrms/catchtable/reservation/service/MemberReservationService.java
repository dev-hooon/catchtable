package com.prgrms.catchtable.reservation.service;

import static com.prgrms.catchtable.common.exception.ErrorCode.ALREADY_OCCUPIED_RESERVATION_TIME;
import static com.prgrms.catchtable.common.exception.ErrorCode.ALREADY_PREOCCUPIED_RESERVATION_TIME;
import static com.prgrms.catchtable.common.exception.ErrorCode.NOT_EXIST_OWNER;
import static com.prgrms.catchtable.common.exception.ErrorCode.NOT_EXIST_RESERVATION;
import static com.prgrms.catchtable.common.exception.ErrorCode.NOT_EXIST_TIME;
import static com.prgrms.catchtable.common.notification.NotificationContent.RESERVATION_CANCELLED;
import static com.prgrms.catchtable.common.notification.NotificationContent.RESERVATION_COMPLETED;
import static com.prgrms.catchtable.reservation.domain.ReservationStatus.CANCELLED;
import static com.prgrms.catchtable.reservation.domain.ReservationStatus.COMPLETED;
import static com.prgrms.catchtable.reservation.dto.mapper.ReservationMapper.toCancelReservationResponse;
import static com.prgrms.catchtable.reservation.dto.mapper.ReservationMapper.toCreateReservationResponse;
import static com.prgrms.catchtable.reservation.dto.mapper.ReservationMapper.toModifyReservationResponse;
import static java.lang.Boolean.FALSE;

import com.prgrms.catchtable.common.exception.custom.BadRequestCustomException;
import com.prgrms.catchtable.common.exception.custom.NotFoundCustomException;
import com.prgrms.catchtable.common.notification.NotificationContent;
import com.prgrms.catchtable.member.domain.Member;
import com.prgrms.catchtable.notification.dto.request.SendMessageToMemberRequest;
import com.prgrms.catchtable.notification.dto.request.SendMessageToOwnerRequest;
import com.prgrms.catchtable.owner.domain.Owner;
import com.prgrms.catchtable.owner.repository.OwnerRepository;
import com.prgrms.catchtable.reservation.domain.Reservation;
import com.prgrms.catchtable.reservation.domain.ReservationTime;
import com.prgrms.catchtable.reservation.dto.mapper.ReservationMapper;
import com.prgrms.catchtable.reservation.dto.request.CreateReservationRequest;
import com.prgrms.catchtable.reservation.dto.request.ModifyReservationRequest;
import com.prgrms.catchtable.reservation.dto.response.CancelReservationResponse;
import com.prgrms.catchtable.reservation.dto.response.CreateReservationResponse;
import com.prgrms.catchtable.reservation.dto.response.GetAllReservationResponse;
import com.prgrms.catchtable.reservation.dto.response.ModifyReservationResponse;
import com.prgrms.catchtable.reservation.repository.ReservationLockRepository;
import com.prgrms.catchtable.reservation.repository.ReservationRepository;
import com.prgrms.catchtable.reservation.repository.ReservationTimeRepository;
import com.prgrms.catchtable.shop.domain.Shop;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberReservationService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationAsync reservationAsync;
    private final ReservationLockRepository reservationLockRepository;
    private final OwnerRepository ownerRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public CreateReservationResponse preOccupyReservation(Member member,
        CreateReservationRequest request) {
        Long reservationTimeId = request.reservationTimeId();
        while (FALSE.equals(reservationLockRepository.lock(reservationTimeId))) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        ReservationTime reservationTime = reservationTimeRepository.findByIdWithShop(reservationTimeId)
            .orElseThrow(() -> {
                    reservationLockRepository.unlock(reservationTimeId);
                    return new NotFoundCustomException(NOT_EXIST_TIME);
                }
            );

        validateIsPreOccupied(reservationTime);

        reservationAsync.setPreOcuppied(reservationTime);
        Shop shop = reservationTime.getShop();
        reservationLockRepository.unlock(reservationTimeId);

        return CreateReservationResponse.builder()
            .shopName(shop.getName())
            .memberName(member.getName())
            .date(reservationTime.getTime())
            .peopleCount(request.peopleCount())
            .build();
    }

    @Transactional
    public CreateReservationResponse registerReservation(Member member,
        CreateReservationRequest request) {
        ReservationTime reservationTime = reservationTimeRepository.findByIdWithShop(
                request.reservationTimeId()).
            orElseThrow(() -> new NotFoundCustomException(NOT_EXIST_TIME));

        validateIsOccupied(reservationTime);

        reservationTime.setOccupiedTrue();

        Reservation reservation = Reservation.builder()
            .status(COMPLETED)
            .peopleCount(request.peopleCount())
            .reservationTime(reservationTime)
            .member(member)
            .build();
        Reservation savedReservation = reservationRepository.save(reservation);

        sendMessageToMemberAndOwner(member, reservationTime, RESERVATION_COMPLETED); //점주와 회원에게 알림 발송

        return toCreateReservationResponse(savedReservation);
    }

    @Transactional(readOnly = true)
    public List<GetAllReservationResponse> getAllReservation(Member member) {
        List<Reservation> reservations = reservationRepository.findAllWithReservationTimeAndShopByMemberId(
            member);
        return reservations.stream()
            .map(ReservationMapper::toGetAllReservationRepsonse)
            .toList();
    }

    @Transactional
    public ModifyReservationResponse modifyReservation(Long reservavtionId,
        ModifyReservationRequest request) {
        Reservation reservation = reservationRepository.findByIdWithReservationTimeAndShop(
                reservavtionId)
            .orElseThrow(() -> new BadRequestCustomException(NOT_EXIST_RESERVATION)); //예약 Id로 예약 조회
        Shop shop = reservation.getShop();

        ReservationTime reservationTime = reservationTimeRepository.findByIdAndShopId(
                request.reservationTimeId(), shop.getId())
            .orElseThrow(
                () -> new BadRequestCustomException(NOT_EXIST_TIME)); // 예약한 매장의 수정하려는 시간을 조회

        validateIsPreOccupied(reservationTime); // 예약시간이 선점되었는 지 확인

        validateIsOccupied(reservationTime); // 예약시간이 이미 차지되었는 지 확인

        reservation.getReservationTime().setOccupiedFalse(); // 기존 예약의 예약시간 예약가능으로 변경

        reservation.modifyReservation(reservationTime,
            request.peopleCount()); // 예약 필드 값 수정하는 엔티티의 메소드

        return toModifyReservationResponse(reservation);
    }

    @Transactional
    public CancelReservationResponse cancelReservation(Member member, Long reservationId) {
        Reservation reservation = reservationRepository.findByIdWithReservationTimeAndShop(
                reservationId)
            .orElseThrow(() -> new NotFoundCustomException(NOT_EXIST_RESERVATION));

        reservation.changeStatus(CANCELLED); // 해당 예약 상태 취소로 변경

        ReservationTime reservationTime = reservation.getReservationTime(); // 해당 예약의 예약시간 차지 여부 true로 변경

        reservationTime.setOccupiedFalse();

        sendMessageToMemberAndOwner(member, reservationTime, RESERVATION_CANCELLED);

        return toCancelReservationResponse(reservation);
    }

    private void sendMessageToMemberAndOwner(Member member, ReservationTime reservationTime, NotificationContent content) {

        Owner owner = ownerRepository.findOwnerByShop(reservationTime.getShop())
            .orElseThrow(() -> new NotFoundCustomException(NOT_EXIST_OWNER));

        SendMessageToMemberRequest sendMessageToMember = new SendMessageToMemberRequest(
            member,
            content.apply(reservationTime.getTime().toString())
        ); // 회원에게 보낼 해당 시간대의 예약 완료 알림 생성

        SendMessageToOwnerRequest sendMessageToOwner = new SendMessageToOwnerRequest(owner,
            content.apply(
                reservationTime.getTime().toString())); // 해당 시간의 예약 취소 메세지 dto 생성

        publisher.publishEvent(sendMessageToMember);
        publisher.publishEvent(sendMessageToOwner); // 취소한 예약의 매장 점주에게 예약 취소 알림 발송
    }

    private void validateIsPreOccupied(ReservationTime reservationTime) {
        if (reservationTime.isPreOccupied()) {
            reservationLockRepository.unlock(reservationTime.getId());
            throw new BadRequestCustomException(ALREADY_PREOCCUPIED_RESERVATION_TIME);
        }
    }

    private void validateIsOccupied(ReservationTime reservationTime) {
        if (reservationTime.isOccupied()) {
            reservationLockRepository.unlock(reservationTime.getId());
            throw new BadRequestCustomException(ALREADY_OCCUPIED_RESERVATION_TIME);
        }
    }
}
