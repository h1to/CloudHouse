package com.cloudhouse.booking.service.client;

import ch.qos.logback.classic.Logger;
import com.cloudhouse.booking.entity.booking.*;
import com.cloudhouse.booking.repository.AddServiceRepo;
import com.cloudhouse.booking.repository.RoomTypeRepo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RoomTypeService {

    private RoomTypeRepo roomTypeRepo;

    private AddServiceRepo addServiceRepo;

    private static final Logger logger = (Logger) LoggerFactory.getLogger(RoomTypeService.class);

    @Autowired
    public RoomTypeService(RoomTypeRepo roomTypeRepo, AddServiceRepo addServiceRepo) {
        this.roomTypeRepo = roomTypeRepo;
        this.addServiceRepo = addServiceRepo;
    }

    @CircuitBreaker(name = "bookingService")
    public List<RoomType> findAll() {
        return roomTypeRepo.findAll();
    }

    @CircuitBreaker(name = "bookingService")
    public RoomType findRoomTypeById(Long id) {
        return roomTypeRepo.findByIdType(id);
    }

    @CircuitBreaker(name = "bookingService")
    public List<RoomType> findAllByPersons(Integer persons) {
        return roomTypeRepo.findAllByPersonsIsGreaterThanEqual(persons);
    }

    @CircuitBreaker(name = "bookingService")
    public BigDecimal calcRoomPrice(Booking booking) {
        List<PricePeriod> pricePeriods = roomTypeRepo.findByIdType(booking.getRoom().getRoomType().getIdType()).getPrice();
        BigDecimal price = new BigDecimal(0);

        PricePeriod first = new PricePeriod();
        PricePeriod second = new PricePeriod();
        PricePeriod middle = new PricePeriod();
        for (int i = 0; i < pricePeriods.size(); i++) {
            logger.info("In one period calculation...");
            if (pricePeriods.get(i).getStartDate().isBefore(booking.getCheckIn()) && pricePeriods.get(i).getEndDate().isAfter(booking.getCheckIn())) {
                first = pricePeriods.get(i);
                if (pricePeriods.get(i).getStartDate().isBefore(booking.getCheckOut()) && pricePeriods.get(i).getEndDate().isAfter(booking.getCheckOut())) {
                    price = price.add(
                            pricePeriods.get(i).getPrice().multiply(
                                    BigDecimal.valueOf(
                                            ChronoUnit.DAYS.between(booking.getCheckIn(),booking.getCheckOut()))));
                    logger.info("Price for living for 2 adults: " + price);

                    return price;
                }
            }
            else if (pricePeriods.get(i).getStartDate().isBefore(booking.getCheckOut()) && pricePeriods.get(i).getEndDate().isAfter(booking.getCheckOut())) {
                second = pricePeriods.get(i);
            }
            else {
                middle = pricePeriods.get(i);
            }
        }

        logger.info("First period: " + first);
        logger.info("Second period: " + second);

        price = price.add(
                first.getPrice().multiply(
                        BigDecimal.valueOf(
                                ChronoUnit.DAYS.between(
                                        booking.getCheckIn(),
                                        first.getEndDate()) + 1)));

        price = price.add(
                second.getPrice().multiply(
                        BigDecimal.valueOf(
                                ChronoUnit.DAYS.between(
                                        second.getStartDate(),booking.getCheckOut()))
                )
        );

        if (ChronoUnit.DAYS.between(first.getEndDate(),second.getStartDate()) > 1) {
            price = price.add(
                    middle.getPrice().multiply(
                            BigDecimal.valueOf(
                                    ChronoUnit.DAYS.between(
                                            middle.getStartDate(),middle.getEndDate()) + 1)
                    )
            );
        }

        logger.info("Price for living for 2 adults: " + price);
        return price;
    }

}
