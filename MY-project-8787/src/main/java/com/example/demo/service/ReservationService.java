package com.example.demo.service;

import java.util.List;

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

    // 創建預約並發送 WebSocket 消息
    public void createReservation(ReservationDto reservationDto) {
        String sql = "INSERT INTO reservations (email, date, time, equipment, field) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, reservationDto.getEmail(), reservationDto.getDate(), reservationDto.getTime(), reservationDto.isEquipment(), reservationDto.getField());

        // 發送消息到 WebSocket
        messagingTemplate.convertAndSend("/topic/reservations", reservationDto);
    }

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
    
    //ranking
    public UserRankingDto getUserRanking(String email) {
        String sql = "SELECT email, COUNT(*) as reservation_count FROM reservations WHERE email = ? GROUP BY email";
        List<UserRankingDto> rankings = jdbcTemplate.query(sql, new Object[]{email}, new BeanPropertyRowMapper<>(UserRankingDto.class));
        return rankings.isEmpty() ? null : rankings.get(0);
    }
}