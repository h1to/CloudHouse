package com.cloudhouse.booking.repository;

import com.cloudhouse.booking.entity.booking.AdditionalService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddServiceRepo extends JpaRepository<AdditionalService,Long> {

    List<AdditionalService> findAllByIdServiceIn(List<Long> ids);

    AdditionalService findByIdService(Long id);

}
