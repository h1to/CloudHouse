package com.cloudhouse.booking;

import com.cloudhouse.booking.entity.booking.BookingStatus;
import com.cloudhouse.booking.entity.booking.EBookingStatus;
import com.cloudhouse.booking.entity.booking.PricePeriod;
import com.cloudhouse.booking.entity.booking.RoomType;
import com.cloudhouse.booking.repository.BookingStatusRepo;
import com.cloudhouse.booking.repository.PricePeriodRepo;
import com.cloudhouse.booking.repository.RoomTypeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.cloudhouse.booking.config.StaticDataLoader.getUserId;

@SpringBootApplication
@EnableJpaRepositories
@RefreshScope
@EnableFeignClients
public class BookingApplication implements CommandLineRunner {

	@Autowired
	RoomTypeRepo roomTypeRepo;

	@Autowired
	PricePeriodRepo pricePeriodRepo;

	public static void main(String[] args) {
		SpringApplication.run(BookingApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {



	}
}
