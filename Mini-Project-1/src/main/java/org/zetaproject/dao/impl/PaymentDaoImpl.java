package org.zetaproject.dao.impl;

import org.zetaproject.dao.PaymentDao;
import org.zetaproject.model.entites.Payment;
import org.zetaproject.model.enums.PaymentCategory;
import org.zetaproject.model.enums.PaymentStatus;
import org.zetaproject.model.enums.PaymentType;
import org.zetaproject.utils.DBUtil;

import java.sql.*;
import java.util.*;

public class PaymentDaoImpl implements PaymentDao {
    @Override
    public void addPayment(Payment payment) throws Exception {
        String sql = "INSERT INTO payment (amount, type, category, status, created_at, updated_at, user_id, remarks) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, payment.getAmount());
            ps.setString(2, payment.getType().name());
            ps.setString(3, payment.getCategory().name());
            ps.setString(4, payment.getStatus().name());
            ps.setTimestamp(5, new java.sql.Timestamp(payment.getCreatedAt().getTime()));
            ps.setTimestamp(6, new java.sql.Timestamp(payment.getUpdatedAt().getTime()));
            ps.setInt(7, payment.getUserId());
            ps.setString(8, payment.getRemarks());
            ps.executeUpdate();
        }
    }

    @Override
    public void updatePaymentStatus(int paymentId, String status) throws Exception {
        String sql = "UPDATE payment SET status = ?, updated_at = NOW() WHERE id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, paymentId);
            ps.executeUpdate();
        }
    }

    @Override
    public Payment getPayment(int paymentId) throws Exception {
        String sql = "SELECT * FROM payment WHERE id = ?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Payment payment = new Payment();
                payment.setId(rs.getInt("id"));
                payment.setAmount(rs.getDouble("amount"));
                payment.setType(PaymentType.valueOf(rs.getString("type")));
                payment.setCategory(PaymentCategory.valueOf(rs.getString("category")));
                payment.setStatus(PaymentStatus.valueOf(rs.getString("status")));
                payment.setCreatedAt(rs.getTimestamp("created_at"));
                payment.setUpdatedAt(rs.getTimestamp("updated_at"));
                payment.setUserId(rs.getInt("user_id"));
                payment.setRemarks(rs.getString("remarks"));
                return payment;
            } else {
                return null;
            }
        }
    }

    @Override
    public List<Payment> getAllPayments() throws Exception {
        String sql = "SELECT * FROM payment";
        List<Payment> payments = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Payment payment = new Payment();
                payment.setId(rs.getInt("id"));
                payment.setAmount(rs.getDouble("amount"));
                payment.setType(PaymentType.valueOf(rs.getString("type")));
                payment.setCategory(PaymentCategory.valueOf(rs.getString("category")));
                payment.setStatus(PaymentStatus.valueOf(rs.getString("status")));
                payment.setCreatedAt(rs.getTimestamp("created_at"));
                payment.setUpdatedAt(rs.getTimestamp("updated_at"));
                payment.setUserId(rs.getInt("user_id"));
                payment.setRemarks(rs.getString("remarks"));
                payments.add(payment);
            }
        }
        return payments;
    }

    @Override
    public List<Payment> getPaymentsByUser(int userId) throws Exception {
        String sql = "SELECT * FROM payment WHERE user_id = ?";
        List<Payment> payments = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Payment payment = new Payment();
                payment.setId(rs.getInt("id"));
                payment.setAmount(rs.getDouble("amount"));
                payment.setType(PaymentType.valueOf(rs.getString("type")));
                payment.setCategory(PaymentCategory.valueOf(rs.getString("category")));
                payment.setStatus(PaymentStatus.valueOf(rs.getString("status")));
                payment.setCreatedAt(rs.getTimestamp("created_at"));
                payment.setUpdatedAt(rs.getTimestamp("updated_at"));
                payment.setUserId(rs.getInt("user_id"));
                payment.setRemarks(rs.getString("remarks"));
                payments.add(payment);
            }
        }
        return payments;
    }
}
