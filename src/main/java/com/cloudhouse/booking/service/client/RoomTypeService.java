package com.cloudhouse.booking.service.client;

import com.cloudhouse.booking.entity.booking.PricePeriod;
import com.cloudhouse.booking.entity.booking.RoomType;
import com.cloudhouse.booking.repository.RoomTypeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RoomTypeService {

    private RoomTypeRepo roomTypeRepo;

    @Autowired
    public RoomTypeService(RoomTypeRepo roomTypeRepo) {
        this.roomTypeRepo = roomTypeRepo;
    }


    public RoomType findRoomTypeById(Long id) {
        return roomTypeRepo.findByIdType(id);
    }

    public BigDecimal calcRoomPrice(LocalDateTime checkIn, LocalDateTime checkOut, RoomType roomType) {
        List<PricePeriod> pricePeriods = roomTypeRepo.findByIdType(roomType.getIdType()).getPrice();
        BigDecimal price = new BigDecimal(0);

        PricePeriod first = new PricePeriod();
        PricePeriod second = new PricePeriod();
        PricePeriod middle = new PricePeriod();
        for (int i = 0; i < pricePeriods.size(); i++) {
            if (pricePeriods.get(i).getStartDate().isBefore(checkIn) && pricePeriods.get(i).getEndDate().isAfter(checkIn)) {
                first = pricePeriods.get(i);
                if (pricePeriods.get(i).getStartDate().isBefore(checkOut) && pricePeriods.get(i).getEndDate().isAfter(checkOut)) {
                    price = price.add(
                            pricePeriods.get(i).getPrice().multiply(
                                    BigDecimal.valueOf(
                                            ChronoUnit.DAYS.between(checkIn,checkOut))));
                    return price;
                }
            }
            else if (pricePeriods.get(i).getStartDate().isBefore(checkOut) && pricePeriods.get(i).getEndDate().isAfter(checkOut)) {
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
                                        checkIn,
                                        first.getEndDate()) + 1)));

        price = price.add(
                second.getPrice().multiply(
                        BigDecimal.valueOf(
                                ChronoUnit.DAYS.between(
                                        second.getStartDate(),checkOut))
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
