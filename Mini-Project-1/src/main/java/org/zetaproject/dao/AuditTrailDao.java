package org.zetaproject.dao;

import org.zetaproject.model.entites.AuditTrail;

public interface AuditTrailDao {
    void logAction(AuditTrail auditTrail) throws Exception;
}
