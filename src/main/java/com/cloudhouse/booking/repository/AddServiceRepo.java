package com.cloudhouse.booking.repository;

import com.cloudhouse.booking.entity.booking.AdditionalService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddServiceRepo extends JpaRepository<AdditionalService,Long> {



}
