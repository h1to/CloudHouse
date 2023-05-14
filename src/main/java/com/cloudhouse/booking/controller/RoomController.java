package com.cloudhouse.booking.controller;

import com.cloudhouse.booking.entity.Response;
import com.cloudhouse.booking.entity.booking.Room;
import com.cloudhouse.booking.service.client.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("rooms")
public class RoomController {

    private RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @RolesAllowed({"admin","manager"})
    @GetMapping
    public Response<List<Room>> getAllRooms() {
        Response<List<Room>> response = new Response<>();
        response.setData(roomService.getAllRooms());

        response.setStatusCode(0);
        response.setMsg("Success");
        return response;
    }

}
