package org.zetaproject.model.entites;

import java.util.Date;

public class AuditTrail {
    private int id;
    private String action;
    private int userId;
    private int paymentId;
    private Date timestamp;
    private String details;

    public AuditTrail() {}

    public AuditTrail(int id, String action, int userId, int paymentId, Date timestamp, String details) {
        this.id = id;
        this.action = action;
        this.userId = userId;
        this.paymentId = paymentId;
        this.timestamp = timestamp;
        this.details = details;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
