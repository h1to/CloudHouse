package com.cloudhouse.booking.repository;

import com.cloudhouse.booking.entity.booking.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTypeRepo extends JpaRepository<RoomType,Long> {

    RoomType findByIdType(Long id);

}
