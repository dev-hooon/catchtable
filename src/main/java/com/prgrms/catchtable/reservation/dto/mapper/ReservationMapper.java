package com.prgrms.catchtable.reservation.dto.mapper;

import static lombok.AccessLevel.PRIVATE;

import com.prgrms.catchtable.reservation.domain.Reservation;
import com.prgrms.catchtable.reservation.dto.response.CancelReservationResponse;
import com.prgrms.catchtable.reservation.dto.response.CreateReservationResponse;
import com.prgrms.catchtable.reservation.dto.response.GetAllReservationResponse;
import com.prgrms.catchtable.reservation.dto.response.ModifyReservationResponse;
import com.prgrms.catchtable.reservation.dto.response.OwnerGetAllReservationResponse;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class ReservationMapper {

    public static CreateReservationResponse toCreateReservationResponse(Reservation reservation) {
        return CreateReservationResponse.builder()
            .shopName(reservation.getShop().getName())
            .memberName(reservation.getMember().getName())
            .date(reservation.getReservationTime().getTime())
            .peopleCount(reservation.getPeopleCount())
            .build();
    }

    public static GetAllReservationResponse toGetAllReservationRepsonse(Reservation reservation) {
        return GetAllReservationResponse.builder()
            .reservationId(reservation.getId())
            .date(reservation.getReservationTime().getTime())
            .shopName(reservation.getShop().getName())
            .peopleCount(reservation.getPeopleCount())
            .status(reservation.getStatus())
            .build();
    }

    public static ModifyReservationResponse toModifyReservationResponse(Reservation reservation) {
        return ModifyReservationResponse.builder()
            .shopName(reservation.getShop().getName())
            .memberName(reservation.getMember().getName())
            .date(reservation.getReservationTime().getTime())
            .peopleCount(reservation.getPeopleCount())
            .build();
    }

    public static CancelReservationResponse toCancelReservationResponse(Reservation reservation) {
        return CancelReservationResponse.builder()
            .status(reservation.getStatus())
            .build();
    }

    public static OwnerGetAllReservationResponse toOwnerGetAllReservationResponse(
        Reservation reservation) {
        return OwnerGetAllReservationResponse.builder()
            .reservationId(reservation.getId())
            .date(reservation.getReservationTime().getTime())
            .peopleCount(reservation.getPeopleCount())
            .status(reservation.getStatus())
            .build();
    }

}
