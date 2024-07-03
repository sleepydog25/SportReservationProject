package com.example.demo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.modal.dto.ReservationDto;
import com.example.demo.modal.dto.UserRankingDto;

@Service
public class ReservationService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // 獲取當前時間之後的預約
    public List<ReservationDto> getReservationsAfterNow(String email) {
        String sql = "SELECT id, email, date, time, equipment, field FROM reservations WHERE email = ? AND STR_TO_DATE(CONCAT(date, ' ', LEFT(time, 5)), '%Y-%m-%d %H:%i') > NOW()";
        return jdbcTemplate.query(sql, new Object[]{email}, new BeanPropertyRowMapper<>(ReservationDto.class));
    }

    // 獲取當前時間之前的預約
    public List<ReservationDto> getReservationsBeforeNow(String email) {
        String sql = "SELECT id, email, date, time, equipment, field FROM reservations WHERE email = ? AND STR_TO_DATE(CONCAT(date, ' ', LEFT(time, 5)), '%Y-%m-%d %H:%i') < NOW()";
        return jdbcTemplate.query(sql, new Object[]{email}, new BeanPropertyRowMapper<>(ReservationDto.class));
    }

//    // 創建預約並發送 WebSocket 消息
//    public void createReservation(ReservationDto reservationDto) {
//        String sql = "INSERT INTO reservations (email, date, time, equipment, field) VALUES (?, ?, ?, ?, ?)";
//        jdbcTemplate.update(sql, reservationDto.getEmail(), reservationDto.getDate(), reservationDto.getTime(), reservationDto.isEquipment(), reservationDto.getField());
//
//        // 發送消息到 WebSocket
//        messagingTemplate.convertAndSend("/topic/reservations", reservationDto);
//    }
    
    

    // 更新預約
    public void updateReservation(ReservationDto reservationDto) {
        String sql = "UPDATE reservations SET email = ?, date = ?, time = ?, equipment = ?, field = ? WHERE id = ?";
        jdbcTemplate.update(sql, reservationDto.getEmail(), reservationDto.getDate(), reservationDto.getTime(), reservationDto.isEquipment(), reservationDto.getField(), reservationDto.getId());
    }

    // 取消預約
    public void cancelReservation(Long id) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    // 預約狀態
    public boolean isFieldReserved(String field) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE field = ? AND NOW() BETWEEN STR_TO_DATE(CONCAT(date, ' ', LEFT(time, 5)), '%Y-%m-%d %H:%i') AND STR_TO_DATE(CONCAT(date, ' ', RIGHT(time, 5)), '%Y-%m-%d %H:%i')";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{field}, Integer.class);
        return count != null && count > 0;
    }

    // 獲取所有預約紀錄
    public List<ReservationDto> getAllReservations() {
        String sql = "SELECT id, email, date, time, equipment, field FROM reservations";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ReservationDto.class));
    }
    
    //old ranking
//    public UserRankingDto getUserRanking(String email) {
//        String sql = "SELECT email, COUNT(*) as reservation_count FROM reservations WHERE email = ? GROUP BY email";
//        List<UserRankingDto> rankings = jdbcTemplate.query(sql, new Object[]{email}, new BeanPropertyRowMapper<>(UserRankingDto.class));
//        return rankings.isEmpty() ? null : rankings.get(0);
//    }
    
    //duplicate booking solution
    public List<String> getReservedTimeslots(String date, String field) {
        String sql = "SELECT time FROM reservations WHERE date = ? AND field = ?";
        return jdbcTemplate.queryForList(sql, new Object[]{date, field}, String.class);
    }

    public Map<String, String> createReservation(ReservationDto reservation) {
        String sql = "INSERT INTO reservations (email, date, time, equipment, field) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, reservation.getEmail(), reservation.getDate(), reservation.getTime(), reservation.isEquipment(), reservation.getField());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Reservation successful");

        //send WebSocket message
        messagingTemplate.convertAndSend("/topic/reservations", reservation);

        return response;
    }
    
    //new ranking
    public int getUserReservationCount(String email) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE email = ? AND STR_TO_DATE(CONCAT(date, ' ', LEFT(time, 5)), '%Y-%m-%d %H:%i') < NOW()";
        return jdbcTemplate.queryForObject(sql, new Object[]{email}, Integer.class);
    }

    public int getTotalUserCount() {
        String sql = "SELECT COUNT(DISTINCT email) FROM reservations WHERE STR_TO_DATE(CONCAT(date, ' ', LEFT(time, 5)), '%Y-%m-%d %H:%i') < NOW()";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public int getUserRank(String email) {
        String sql = "SELECT email, COUNT(*) as reservation_count FROM reservations " +
                     "WHERE STR_TO_DATE(CONCAT(date, ' ', LEFT(time, 5)), '%Y-%m-%d %H:%i') < NOW() " +
                     "GROUP BY email " +
                     "ORDER BY reservation_count DESC";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

        for (int i = 0; i < results.size(); i++) {
            if (results.get(i).get("email").equals(email)) {
                return i + 1; // 因為排名從1開始
            }
        }

        return getTotalUserCount(); // 如果用戶沒有預約，返回總人數作為排名
    }
    

 // 獲取場地名稱和借閱次數，按場地類型和具體場地分類
    public List<Map<String, Object>> getFieldUsageFrequency() {
        String sql = "SELECT field, COUNT(*) as usage_count FROM reservations GROUP BY field";
        return jdbcTemplate.queryForList(sql);
    }
    
    // 獲取借用時段頻率
    public List<Map<String, Object>> getTimeSlotUsageFrequency() {
        String sql = "SELECT time, COUNT(*) as usage_count FROM reservations GROUP BY time";
        return jdbcTemplate.queryForList(sql);
    }
    
    // 獲取是否借用球具的統計數據
    public Map<String, Integer> getEquipmentUsageStatistics() {
        String sql = "SELECT equipment, COUNT(*) as count FROM reservations GROUP BY equipment";
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);

        Map<String, Integer> statistics = new HashMap<>();
        for (Map<String, Object> result : results) {
            boolean equipmentUsed = (boolean) result.get("equipment");
            String key = equipmentUsed ? "借用球具" : "不借用球具";
            statistics.put(key, ((Number) result.get("count")).intValue());
        }
        return statistics;
    }
}