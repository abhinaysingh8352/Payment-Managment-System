package org.zetaproject.dao.impl;

import org.zetaproject.dao.AuditTrailDao;
import org.zetaproject.model.entites.AuditTrail;
import org.zetaproject.utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AuditTrailDaoImpl implements AuditTrailDao {
    @Override
    public void logAction(AuditTrail auditTrail) throws Exception {
        String sql = "INSERT INTO audit_trail (action, user_id, payment_id, timestamp, details) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, auditTrail.getAction());
            ps.setInt(2, auditTrail.getUserId());
            ps.setInt(3, auditTrail.getPaymentId());
            ps.setTimestamp(4, new java.sql.Timestamp(auditTrail.getTimestamp().getTime()));
            ps.setString(5, auditTrail.getDetails());
            ps.executeUpdate();
        }
    }
}
