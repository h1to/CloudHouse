package com.cloudhouse.booking.repository;

import com.cloudhouse.booking.entity.booking.PricePeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PricePeriodRepo extends JpaRepository<PricePeriod,Long> {
}
