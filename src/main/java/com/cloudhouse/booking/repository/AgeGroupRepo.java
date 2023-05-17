package com.cloudhouse.booking.repository;

import com.cloudhouse.booking.entity.booking.AgeGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgeGroupRepo extends JpaRepository<AgeGroup,Long> {



}
