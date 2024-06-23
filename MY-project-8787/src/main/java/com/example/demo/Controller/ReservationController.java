package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Util.JwtTokenUtil;
import com.example.demo.modal.dto.LoginResponse;
import com.example.demo.modal.dto.ReservationDto;
import com.example.demo.modal.dto.UserRankingDto;
import com.example.demo.service.ReservationService;
import com.nimbusds.jose.JOSEException;

import net.glxn.qrgen.QRCode;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody ReservationDto reservationDto) {
        try {
            reservationService.createReservation(reservationDto);
            return ResponseEntity.ok(new LoginResponse(true, "Reservation successful"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse(false, "Reservation failed"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReservation(@PathVariable Long id, @RequestBody ReservationDto reservationDto) {
        try {
            reservationDto.setId(id);
            reservationService.updateReservation(reservationDto);
            return ResponseEntity.ok(new LoginResponse(true, "Reservation updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse(false, "Update failed"));
        }
    }

    @GetMapping("/BeforeNow")
    public ResponseEntity<List<ReservationDto>> getReservationsBeforeNow(@RequestParam String email) {
        try {
            List<ReservationDto> reservations = reservationService.getReservationsBeforeNow(email);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) {
        try {
            reservationService.cancelReservation(id);
            return ResponseEntity.ok(new LoginResponse(true, "Reservation cancelled successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse(false, "Cancellation failed"));
        }
    }

    @GetMapping("/check-status")
    public ResponseEntity<Map<String, String>> checkReservationStatus(@RequestParam String field) {
        try {
            boolean isReserved = reservationService.isFieldReserved(field);
            Map<String, String> response = new HashMap<>();
            response.put("status", isReserved ? "不開放" : "開放中");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/AfterNow")
    public ResponseEntity<?> getReservationsAfterNow(@RequestParam String email) {
        try {
            List<ReservationDto> reservations = reservationService.getReservationsAfterNow(email);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/generate-token")
    public ResponseEntity<String> generateToken(@RequestBody ReservationDto reservation) {
        try {
            String token = JwtTokenUtil.generateToken(reservation);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error generating token: " + e.getMessage());
        }
    }

    @PostMapping("/generate-qrcode")
    public ResponseEntity<byte[]> generateQRCode(@RequestBody String token) {
        try {
            ByteArrayOutputStream stream = QRCode.from(token)
                    .withSize(300, 300)
                    .stream();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(stream.size());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(stream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<ReservationDto>> getAllReservations() {
        List<ReservationDto> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }
    
    @GetMapping("/user-ranking")
    public ResponseEntity<UserRankingDto> getUserRanking(@RequestParam String email) {
        UserRankingDto ranking = reservationService.getUserRanking(email);
        if (ranking != null) {
            return ResponseEntity.ok(ranking);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}