package com.cloudhouse.booking;

import com.cloudhouse.booking.entity.booking.BookingStatus;
import com.cloudhouse.booking.entity.booking.EBookingStatus;
import com.cloudhouse.booking.repository.BookingStatusRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@RefreshScope
@EnableFeignClients
public class BookingApplication implements CommandLineRunner {

	BookingStatusRepo bookingStatusRepo;

	@Autowired
	public BookingApplication(BookingStatusRepo bookingStatusRepo) {
		this.bookingStatusRepo = bookingStatusRepo;
	}

	public static void main(String[] args) {
		SpringApplication.run(BookingApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		bookingStatusRepo.save(new BookingStatus(EBookingStatus.NEW,"Just created booking. Not confirmed, yet"));
//		bookingStatusRepo.save(new BookingStatus(EBookingStatus.CONFIRMED,"Confirmed booking"));
//		bookingStatusRepo.save(new BookingStatus(EBookingStatus.ACTIVE,"Active booking means, guests checked in but not checked out"));
//		bookingStatusRepo.save(new BookingStatus(EBookingStatus.CANCELLED,"Cancelled not used booking"));
//		bookingStatusRepo.save(new BookingStatus(EBookingStatus.USED,"Guests success fully checked out and booking no longer active"));
	}
}
