package com.cloudhouse.booking.repository;

import com.cloudhouse.booking.entity.booking.Room;
import com.cloudhouse.booking.entity.booking.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepo extends JpaRepository<Room,Long> {

    Room findByIdRoom(Long idRoom);
    List<Room> findByBuilding(Long idBuilding);

    List<Room> findAllByRoomType(RoomType roomType);

    List<Room> findAllByRoomTypeIn(List<RoomType> roomTypes);

    List<Room> findAllByIdRoomIsNotIn(List<Long> bookedRoomIds);

    List<Room> findAllByIdRoomIsNotInAndRoomType(List<Long> bookedRoomIds, RoomType roomType);


}
