package com.example.spring_ecom.repository.database;

import com.example.spring_ecom.domain.order.SepayTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SepayTransactionRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    private final RowMapper<SepayTransaction> rowMapper = (rs, rowNum) -> new SepayTransaction(
            rs.getLong("id"),
            rs.getInt("sepay_id"),
            rs.getString("gateway"),
            rs.getTimestamp("transaction_date").toLocalDateTime(),
            rs.getString("account_number"),
            rs.getString("sub_account"),
            rs.getBigDecimal("amount_in"),
            rs.getBigDecimal("amount_out"),
            rs.getBigDecimal("accumulated"),
            rs.getString("code"),
            rs.getString("transaction_content"),
            rs.getString("reference_code"),
            rs.getString("description"),
            rs.getString("transfer_type"),
            rs.getBigDecimal("transfer_amount"),
            rs.getBoolean("processed"),
            rs.getObject("order_id", Long.class),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime()
    );
    
    public Optional<SepayTransaction> findBySepayId(Integer sepayId) {
        String sql = "SELECT * FROM sepay_transactions WHERE sepay_id = ?";
        List<SepayTransaction> results = jdbcTemplate.query(sql, rowMapper, sepayId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public Optional<SepayTransaction> findByCode(String code) {
        String sql = "SELECT * FROM sepay_transactions WHERE code = ? AND transfer_type = 'in' ORDER BY created_at DESC LIMIT 1";
        List<SepayTransaction> results = jdbcTemplate.query(sql, rowMapper, code);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public SepayTransaction save(SepayTransaction transaction) {
        if (transaction.id() == null) {
            return insert(transaction);
        } else {
            return update(transaction);
        }
    }
    
    private SepayTransaction insert(SepayTransaction transaction) {
        String sql = """
            INSERT INTO sepay_transactions (
                sepay_id, gateway, transaction_date, account_number, sub_account,
                amount_in, amount_out, accumulated, code, transaction_content,
                reference_code, description, transfer_type, transfer_amount, processed, order_id
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, transaction.sepayId());
            ps.setString(2, transaction.gateway());
            ps.setTimestamp(3, Timestamp.valueOf(transaction.transactionDate()));
            ps.setString(4, transaction.accountNumber());
            ps.setString(5, transaction.subAccount());
            ps.setBigDecimal(6, transaction.amountIn());
            ps.setBigDecimal(7, transaction.amountOut());
            ps.setBigDecimal(8, transaction.accumulated());
            ps.setString(9, transaction.code());
            ps.setString(10, transaction.transactionContent());
            ps.setString(11, transaction.referenceCode());
            ps.setString(12, transaction.description());
            ps.setString(13, transaction.transferType());
            ps.setBigDecimal(14, transaction.transferAmount());
            ps.setBoolean(15, transaction.processed());
            if (transaction.orderId() != null) {
                ps.setLong(16, transaction.orderId());
            } else {
                ps.setNull(16, java.sql.Types.BIGINT);
            }
            return ps;
        }, keyHolder);
        
        Long id = keyHolder.getKey().longValue();
        return new SepayTransaction(
                id,
                transaction.sepayId(),
                transaction.gateway(),
                transaction.transactionDate(),
                transaction.accountNumber(),
                transaction.subAccount(),
                transaction.amountIn(),
                transaction.amountOut(),
                transaction.accumulated(),
                transaction.code(),
                transaction.transactionContent(),
                transaction.referenceCode(),
                transaction.description(),
                transaction.transferType(),
                transaction.transferAmount(),
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
                transaction.gateway(),
                transaction.transactionDate(),
                transaction.accountNumber(),
                transaction.subAccount(),
                transaction.amountIn(),
                transaction.amountOut(),
                transaction.accumulated(),
                transaction.code(),
                transaction.transactionContent(),
                transaction.referenceCode(),
                transaction.description(),
                transaction.transferType(),
                transaction.transferAmount(),
                transaction.processed(),
                transaction.orderId(),
                transaction.createdAt(),
                LocalDateTime.now()
        );
    }
    
    public List<SepayTransaction> findAll(int limit, int offset) {
        String sql = "SELECT * FROM sepay_transactions ORDER BY created_at DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, rowMapper, limit, offset);
    }
    
    public long count() {
        String sql = "SELECT COUNT(*) FROM sepay_transactions";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
}