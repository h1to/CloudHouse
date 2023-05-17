package com.cloudhouse.booking.repository;

import com.cloudhouse.booking.entity.booking.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomTypeRepo extends JpaRepository<RoomType,Long> {

    RoomType findByIdType(Long id);

    List<RoomType> findAllByPersonsIsGreaterThanEqual(Integer persons);

}
