package com.cloudhouse.booking.repository;

import com.cloudhouse.booking.entity.booking.BookingStatus;
import com.cloudhouse.booking.entity.booking.EBookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingStatusRepo extends JpaRepository<BookingStatus,Long> {

    BookingStatus findAllByStatus(EBookingStatus bookingStatus);

}
