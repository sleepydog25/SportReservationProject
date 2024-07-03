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
            return ResponseEntity.ok(new LoginResponse(true, "預約成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse(false, "預約失敗"));
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
    
//    @GetMapping("/user-ranking")
//    public ResponseEntity<UserRankingDto> getUserRanking(@RequestParam String email) {
//        UserRankingDto ranking = reservationService.getUserRanking(email);
//        if (ranking != null) {
//            return ResponseEntity.ok(ranking);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
    
    @GetMapping("/user-ranking")
    public String getUserRanking(@RequestParam String email) {
        int userRank = reservationService.getUserRank(email);
        int totalUsers = reservationService.getTotalUserCount();

        return userRank + "/" + totalUsers;
    }
    
    @GetMapping("/timeslots")
    public ResponseEntity<List<String>> getReservedTimeslots(@RequestParam String date, @RequestParam String field) {
        List<String> reservedTimeslots = reservationService.getReservedTimeslots(date, field);
        return ResponseEntity.ok(reservedTimeslots);
    }
    
    @GetMapping("/field-usage-frequency")
    public ResponseEntity<List<Map<String, Object>>> getFieldUsageFrequency() {
        try {
            List<Map<String, Object>> frequencyData = reservationService.getFieldUsageFrequency();
            return ResponseEntity.ok(frequencyData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/time-slot-usage-frequency")
    public ResponseEntity<List<Map<String, Object>>> getTimeSlotUsageFrequency() {
        try {
            List<Map<String, Object>> frequencyData = reservationService.getTimeSlotUsageFrequency();
            return ResponseEntity.ok(frequencyData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/equipment-usage-statistics")
    public ResponseEntity<Map<String, Integer>> getEquipmentUsageStatistics() {
        try {
            Map<String, Integer> statistics = reservationService.getEquipmentUsageStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

//<div class="calendar">
//<!-- Weekly Calendar -->
//<!--<h5>Weekly Calendar</h5>-->
//<!-- Calendar Title -->
//
//<div class="table-responsive">
//	<!-- Make the table responsive -->
//	<table class="table table-bordered table-dark">
//		<!-- Table with Bootstrap bordered class -->
//		<thead>
//			<!-- Table header row -->
//			<tr>
//				<!-- Time column -->
//				<th>Time</th>
//				<th>Monday</th>
//				<!-- Monday column -->
//				<th>Tuesday</th>
//				<!-- Tuesday column -->
//				<th>Wednesday</th>
//				<!-- Wednesday column -->
//				<th>Thursday</th>
//				<!-- Thursday column -->
//				<th>Friday</th>
//				<!-- Friday column -->
//				<th>Saturday</th>
//				<!-- Saturday column -->
//				<th>Sunday</th>
//				<!-- Sunday column -->
//			</tr>
//		</thead>
//		<tbody>
//			<!-- Example Rows -->
//			<tr>
//				<!-- Time slot -->
//				<td>9:00 - 10:00 AM</td>
//				<td>Event 1</td>
//				<!-- Monday event -->
//				<td></td>
//				<!-- Empty cell for Tuesday -->
//				<td></td>
//				<!-- Empty cell for Wednesday -->
//				<td>Event 2</td>
//				<!-- Thursday event -->
//				<td></td>
//				<!-- Empty cell for Friday -->
//				<td></td>
//				<!-- Empty cell for Saturday -->
//				<td></td>
//				<!-- Empty cell for Sunday -->
//			</tr>
//			<tr>
//				<!-- Time slot -->
//				<td>10:00 - 11:00 AM</td>
//				<td></td>
//				<!-- Empty cell for Monday -->
//				<td>Event 3</td>
//				<!-- Tuesday event -->
//				<td></td>
//				<!-- Empty cell for Wednesday -->
//				<td></td>
//				<!-- Empty cell for Thursday -->
//				<td>Event 4</td>
//				<!-- Friday event -->
//				<td></td>
//				<!-- Empty cell for Saturday -->
//				<td></td>
//				<!-- Empty cell for Sunday -->
//			</tr>
//			<!-- More rows as needed -->
//		</tbody>
//	</table>
//</div>
//</div>
//</div>
//</div>
//</div>
//</div>