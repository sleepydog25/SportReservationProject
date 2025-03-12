package com.example.demo.Controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.example.demo.modal.dto.ReservationDto;

@Controller
public class WebSocketController {

    @MessageMapping("/sendReservation")
    @SendTo("/topic/reservations")
    public ReservationDto sendReservation(ReservationDto reservation) {
        return reservation;
    }
}