package org.zetaproject.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.zetaproject.model.entites.Payment;
import org.zetaproject.model.enums.PaymentCategory;
import org.zetaproject.model.enums.PaymentStatus;
import org.zetaproject.model.enums.PaymentType;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class PaymentDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void create(Payment payment) {
        String sql = "INSERT INTO payments (amount, payment_type, category, status, date, created_by) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        Long generatedId = jdbcTemplate.queryForObject(
                sql,
                new Object[]{payment.getAmount(), payment.getPaymentType().name(), payment.getCategory().name(), payment.getStatus().name(), Timestamp.valueOf(payment.getDate()), payment.getCreatedBy()},
                Long.class
        );
        payment.setId(generatedId);
    }

    public List<Payment> findAll() {
        String sql = "SELECT id, amount, payment_type, category, status, date, created_by FROM payments";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Payment payment = new Payment();
            payment.setId(rs.getLong("id"));
            payment.setAmount(rs.getBigDecimal("amount"));
            payment.setPaymentType(PaymentType.valueOf(rs.getString("payment_type")));
            payment.setCategory(PaymentCategory.valueOf(rs.getString("category")));
            payment.setStatus(PaymentStatus.valueOf(rs.getString("status")));
            payment.setDate(rs.getTimestamp("date").toLocalDateTime());
            payment.setCreatedBy(rs.getLong("created_by"));
            return payment;
        });
    }

    public Payment findById(Long id) {
        String sql = "SELECT id, amount, payment_type, category, status, date, created_by FROM payments WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> {
            Payment payment = new Payment();
            payment.setId(rs.getLong("id"));
            payment.setAmount(rs.getBigDecimal("amount"));
            payment.setPaymentType(PaymentType.valueOf(rs.getString("payment_type")));
            payment.setCategory(PaymentCategory.valueOf(rs.getString("category")));
            payment.setStatus(PaymentStatus.valueOf(rs.getString("status")));
            payment.setDate(rs.getTimestamp("date").toLocalDateTime());
            payment.setCreatedBy(rs.getLong("created_by"));
            return payment;
        });
    }

    public void update(Payment payment) {
        String sql = "UPDATE payments SET amount = ?, payment_type = ?, category = ?, status = ? WHERE id = ?";
        jdbcTemplate.update(sql, payment.getAmount(), payment.getPaymentType().name(), payment.getCategory().name(), payment.getStatus().name(), payment.getId());
    }

    public void delete(Long id) {
        String sql = "DELETE FROM payments WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}