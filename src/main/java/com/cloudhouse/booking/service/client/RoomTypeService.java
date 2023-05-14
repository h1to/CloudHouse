package com.cloudhouse.booking.service.client;

import com.cloudhouse.booking.entity.booking.AdditionalService;
import com.cloudhouse.booking.entity.booking.Booking;
import com.cloudhouse.booking.entity.booking.PricePeriod;
import com.cloudhouse.booking.entity.booking.RoomType;
import com.cloudhouse.booking.repository.AddServiceRepo;
import com.cloudhouse.booking.repository.RoomTypeRepo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RoomTypeService {

    private RoomTypeRepo roomTypeRepo;

    private AddServiceRepo addServiceRepo;

    @Autowired
    public RoomTypeService(RoomTypeRepo roomTypeRepo, AddServiceRepo addServiceRepo) {
        this.roomTypeRepo = roomTypeRepo;
        this.addServiceRepo = addServiceRepo;
    }

    public RoomType findRoomTypeById(Long id) {
        return roomTypeRepo.findByIdType(id);
    }

    public List<RoomType> findAllByPersons(Integer persons) {
        return roomTypeRepo.findAllByPersonsGreaterThanEqual(persons);
    }

    @CircuitBreaker(name = "bookingService")
    public BigDecimal calcRoomPrice(Booking booking) {
        List<PricePeriod> pricePeriods = roomTypeRepo.findByIdType(booking.getRoom().getRoomType().getIdType()).getPrice();
        BigDecimal price = new BigDecimal(0);

        if (booking.getThreeTimesMeal()) {
            AdditionalService meals = addServiceRepo.findByIdService(1L);
            price = price.add(meals.getPrice().multiply(
                    BigDecimal.valueOf(
                            ChronoUnit.DAYS.between(booking.getCheckIn(),booking.getCheckOut()))));

            if (booking.getKids() != null && !booking.getKids().isEmpty()) {
                price = price.add(meals.getKidsPrice().multiply(
                        BigDecimal.valueOf(
                                ChronoUnit.DAYS.between(booking.getCheckIn(),booking.getCheckOut()))));
            }
        }

        PricePeriod first = new PricePeriod();
        PricePeriod second = new PricePeriod();
        PricePeriod middle = new PricePeriod();
        for (int i = 0; i < pricePeriods.size(); i++) {
            if (pricePeriods.get(i).getStartDate().isBefore(booking.getCheckIn()) && pricePeriods.get(i).getEndDate().isAfter(booking.getCheckIn())) {
                first = pricePeriods.get(i);
                if (pricePeriods.get(i).getStartDate().isBefore(booking.getCheckOut()) && pricePeriods.get(i).getEndDate().isAfter(booking.getCheckOut())) {
                    price = price.add(
                            pricePeriods.get(i).getPrice().multiply(
                                    BigDecimal.valueOf(
                                            ChronoUnit.DAYS.between(booking.getCheckIn(),booking.getCheckOut()))));

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
        return price;
    }

}
