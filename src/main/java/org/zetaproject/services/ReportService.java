package org.zetaproject.services;

import org.zetaproject.dao.PaymentDao;
import org.zetaproject.model.entites.Payment;
import org.zetaproject.utils.DBUtil;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class ReportService {

    private final PaymentDao paymentDao;

    public ReportService(PaymentDao paymentDao) {
        this.paymentDao = paymentDao;
    }

    public List<Payment> getAllPayments() throws Exception {
        return paymentDao.getAllPayments();
    }

    public Map<String, Double> getMonthlyReport(int month, int year) throws Exception {
        String sql = "SELECT category, SUM(amount) as total_amount FROM payment " +
                "WHERE EXTRACT(MONTH FROM created_at) = ? AND EXTRACT(YEAR FROM created_at) = ? AND status = ? " +
                "GROUP BY category";

        Map<String, Double> report = new HashMap<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            ps.setString(3, "COMPLETED");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                report.put(rs.getString("category"), rs.getDouble("total_amount"));
            }
        }
        return report;
    }

    public Map<String, Double> getQuarterlyReport(int quarter, int year) throws Exception {
        String sql = "SELECT category, SUM(amount) as total_amount FROM payment " +
                "WHERE EXTRACT(QUARTER FROM created_at) = ? AND EXTRACT(YEAR FROM created_at) = ? AND status = ? " +
                "GROUP BY category";

        Map<String, Double> report = new HashMap<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quarter);
            ps.setInt(2, year);
            ps.setString(3, "COMPLETED");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                report.put(rs.getString("category"), rs.getDouble("total_amount"));
            }
        }
        return report;
    }

    // Returns list of map where each map represents detailed payment with audit info if any
    public List<Map<String, Object>> getCustomReport(Date startDate, Date endDate) throws Exception {
        String sql = "SELECT p.id, p.amount, p.type, p.category, p.status, p.created_at, p.updated_at, p.user_id, p.remarks, " +
                "a.action, a.timestamp as audit_timestamp, a.details " +
                "FROM payment p LEFT JOIN audit_trail a ON p.id = a.payment_id " +
                "WHERE p.created_at >= ? AND p.created_at < ? " +
                "ORDER BY p.created_at ASC, a.timestamp ASC";

        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, new Timestamp(startDate.getTime()));
            ps.setTimestamp(2, new Timestamp(endDate.getTime()));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("paymentId", rs.getInt("id"));
                row.put("amount", rs.getDouble("amount"));
                row.put("type", rs.getString("type"));
                row.put("category", rs.getString("category"));
                row.put("status", rs.getString("status"));
                row.put("createdAt", rs.getTimestamp("created_at"));
                row.put("updatedAt", rs.getTimestamp("updated_at"));
                row.put("userId", rs.getInt("user_id"));
                row.put("remarks", rs.getString("remarks"));
                row.put("auditAction", rs.getString("action"));
                row.put("auditTimestamp", rs.getTimestamp("audit_timestamp"));
                row.put("auditDetails", rs.getString("details"));
                results.add(row);
            }
        }
        return results;
    }
}
