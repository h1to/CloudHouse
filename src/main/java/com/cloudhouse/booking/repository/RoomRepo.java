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

    List<Room> findAllByRoomTypeAndPersons(RoomType roomType, Integer persons);

    List<Room> findAllByPersons(Integer persnons);

    List<Room> findAllByIdRoomIsNotIn(List<Long> bookedRoomIds);

    List<Room> findAllByIdRoomIsNotInAndRoomType(List<Long> bookedRoomIds, RoomType roomType);

    List<Room> findAllByIdRoomIsNotInAndPersons(List<Long> bookedRoomIds, Integer persons);


    List<Room> findAllByIdRoomIsNotInAndRoomTypeAndPersons(List<Long> bookedRoomIds, RoomType roomType, Integer persons);

}
