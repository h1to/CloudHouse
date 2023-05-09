package com.cloudhouse.booking;

import com.cloudhouse.booking.repository.RoomRepo;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableJpaRepositories
@RefreshScope
@EnableFeignClients
public class BookingApplication implements CommandLineRunner {

	private RoomRepo roomRepo;

	@Autowired
	public BookingApplication(RoomRepo roomRepo) {
		this.roomRepo = roomRepo;
	}

	public static void main(String[] args) {
		SpringApplication.run(BookingApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

//		List<Long> ids = new ArrayList<>();
//		ids.add(1L);
//		System.out.println(roomRepo.findAllByIdRoomIsNotInAndPersons(ids,1));
	}
}
