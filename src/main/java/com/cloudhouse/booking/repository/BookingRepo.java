package com.cloudhouse.booking.repository;

import com.cloudhouse.booking.entity.booking.Booking;
import com.cloudhouse.booking.entity.booking.EBookingStatus;
import com.cloudhouse.booking.entity.booking.Room;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking,Long> {

    Booking findByIdBooking(Long id);

    List<Booking> findByRoomAndCheckOutAfter(Room room, Date today);

    @Query(value = "select distinct b.id_room " +
            "from booking b " +
            "where (b.check_in between :checkIn and :checkOut or b.check_out between :checkIn and :checkOut or (b.check_in < :checkIn and b.check_out > :checkOut)) ", nativeQuery = true)
    List<Long> findBookingsOverlap(@Param("checkIn") LocalDateTime checkIn, @Param("checkOut") LocalDateTime checkOut);

    @Query(value = "select distinct b.id_room " +
            "from booking b where (b.check_in between :checkIn and :checkOut or b.check_out between :checkIn and :checkOut or (b.check_in < :checkIn and b.check_out > :checkOut)) " +
            "and b.id_room in (select id_room from room where id_type = :roomType)", nativeQuery = true)
    List<Long> findBookingsOverlap(@Param("checkIn") LocalDateTime checkIn,
                                          @Param("checkOut") LocalDateTime checkOut,
                                          @Param("roomType") Long roomType);

//    @Query(value = "select distinct b.id_room " +
//            "from booking b where (b.check_in between :checkIn and :checkOut or b.check_out between :checkIn and :checkOut or (b.check_in < :checkIn and b.check_out > :checkOut)) " +
//            "and b.id_room in (select id_room from room where id_type = :roomType and persons = :persons)", nativeQuery = true)
//    List<Long> findBookingsOverlap(@Param("checkIn") LocalDateTime checkIn,
//                                                   @Param("checkOut") LocalDateTime checkOut,
//                                                   @Param("roomType") Long roomType,
//                                                   @Param("persons") Integer persons);

//    @Query(value = "select distinct b.id_room " +
//            "from booking b where (b.check_in between :checkIn and :checkOut or b.check_out between :checkIn and :checkOut or (b.check_in < :checkIn and b.check_out > :checkOut)) " +
//            "and b.id_room in (select id_room from room where persons = :persons)", nativeQuery = true)
//    List<Long> findBookingsOverlap(@Param("checkIn") LocalDateTime checkIn,
//                                   @Param("checkOut") LocalDateTime checkOut,
//                                   @Param("persons") Integer persons);


    List<Booking> findAllByUserId(String user);

    List<Booking> findAllByStatusNotAndCheckInAfter(EBookingStatus status, LocalDateTime fromDate);

    @Query(value = "select * from booking where check_in = :date", nativeQuery = true)
    List<Booking> findAllByCheckIn(@Param("date") LocalDateTime date);

    List<Booking> findAllByCheckOut(LocalDateTime date);

}
