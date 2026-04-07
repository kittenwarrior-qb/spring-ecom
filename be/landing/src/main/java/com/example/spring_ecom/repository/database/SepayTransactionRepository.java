package com.example.spring_ecom.repository.database;

import com.example.spring_ecom.domain.order.SepayTransaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SepayTransactionRepository {
    
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    
    private SepayTransaction mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            String webhookDataJson = rs.getString("webhook_data");
            JsonNode webhookData = objectMapper.readTree(webhookDataJson);
            
            return new SepayTransaction(
                    rs.getLong("id"),
                    rs.getInt("sepay_id"),
                    webhookData,
                    rs.getString("code"),
                    rs.getBigDecimal("transfer_amount"),
                    rs.getString("transfer_type"),
                    rs.getBoolean("processed"),
                    rs.getObject("order_id", Long.class),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("updated_at").toLocalDateTime()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse webhook data", e);
        }
    }
    
    public Optional<SepayTransaction> findBySepayId(Integer sepayId) {
        String sql = "SELECT * FROM sepay_transactions WHERE sepay_id = ?";
        List<SepayTransaction> results = jdbcTemplate.query(sql, this::mapRow, sepayId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public Optional<SepayTransaction> findByCode(String code) {
        String sql = "SELECT * FROM sepay_transactions WHERE code = ? AND transfer_type = 'in' ORDER BY created_at DESC LIMIT 1";
        List<SepayTransaction> results = jdbcTemplate.query(sql, this::mapRow, code);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public SepayTransaction save(SepayTransaction transaction) {
        if (Objects.isNull(transaction.id())) {
            return insert(transaction);
        } else {
            return update(transaction);
        }
    }
    
    private SepayTransaction insert(SepayTransaction transaction) {
        String sql = """
            INSERT INTO sepay_transactions (
                sepay_id, webhook_data, code, transfer_amount, transfer_type, processed, order_id
            ) VALUES (?, ?::jsonb, ?, ?, ?, ?, ?)
            """;
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, transaction.sepayId());
            ps.setString(2, transaction.webhookData().toString());
            ps.setString(3, transaction.code());
            ps.setBigDecimal(4, transaction.transferAmount());
            ps.setString(5, transaction.transferType());
            ps.setBoolean(6, transaction.processed());
            if (transaction.orderId() != null) {
                ps.setLong(7, transaction.orderId());
            } else {
                ps.setNull(7, java.sql.Types.BIGINT);
            }
            return ps;
        }, keyHolder);
        
        Long id = keyHolder.getKey().longValue();
        return new SepayTransaction(
                id,
                transaction.sepayId(),
                transaction.webhookData(),
                transaction.code(),
                transaction.transferAmount(),
                transaction.transferType(),
                transaction.processed(),
                transaction.orderId(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
    
    private SepayTransaction update(SepayTransaction transaction) {
        String sql = """
            UPDATE sepay_transactions SET
                processed = ?, order_id = ?, updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
            """;
        
        jdbcTemplate.update(sql, 
                transaction.processed(), 
                transaction.orderId(), 
                transaction.id());
        
        return new SepayTransaction(
                transaction.id(),
                transaction.sepayId(),
                transaction.webhookData(),
                transaction.code(),
                transaction.transferAmount(),
                transaction.transferType(),
                transaction.processed(),
                transaction.orderId(),
                transaction.createdAt(),
                LocalDateTime.now()
        );
    }
    
    public List<SepayTransaction> findAll(int limit, int offset) {
        String sql = "SELECT * FROM sepay_transactions ORDER BY created_at DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, this::mapRow, limit, offset);
    }
    
    public long count() {
        String sql = "SELECT COUNT(*) FROM sepay_transactions";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
}