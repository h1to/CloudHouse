package com.cloudhouse.booking;

import com.cloudhouse.booking.repository.PricePeriodRepo;
import com.cloudhouse.booking.repository.RoomTypeRepo;
import com.cloudhouse.booking.service.client.RoomService;
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

	@Autowired
	RoomTypeRepo roomTypeRepo;

	@Autowired
	PricePeriodRepo pricePeriodRepo;

	@Autowired
	RoomService roomService;

	public static void main(String[] args) {
		SpringApplication.run(BookingApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	}
}
