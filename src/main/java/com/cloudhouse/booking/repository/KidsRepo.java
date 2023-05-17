package com.cloudhouse.booking.repository;

import com.cloudhouse.booking.entity.booking.Kids;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KidsRepo extends JpaRepository<Kids,Long> {



}
