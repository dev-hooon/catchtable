package com.prgrms.catchtable.reservation.dto.mapper;

import static lombok.AccessLevel.PRIVATE;

import com.prgrms.catchtable.reservation.domain.Reservation;
import com.prgrms.catchtable.reservation.dto.response.CreateReservationResponse;
import com.prgrms.catchtable.reservation.dto.response.GetAllReservationResponse;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class ReservationMapper {

    public static CreateReservationResponse toCreateReservationResponse(Reservation reservation) {
        return CreateReservationResponse.builder()
            .shopName(reservation.getShop().getName())
            .memberName("memberA")
            .date(reservation.getReservationTime().getTime())
            .peopleCount(reservation.getPeopleCount())
            .build();
    }

    public static GetAllReservationResponse toGetAllReservationRepsonse(Reservation reservation){
        return GetAllReservationResponse.builder()
            .reservationId(reservation.getId())
            .date(reservation.getReservationTime().getTime())
            .shopName(reservation.getShop().getName())
            .peopleCount(reservation.getPeopleCount())
            .status(reservation.getStatus())
            .build();
    }

}
