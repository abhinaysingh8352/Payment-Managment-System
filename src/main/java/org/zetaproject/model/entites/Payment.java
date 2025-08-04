package org.zetaproject.model.entites;

import org.zetaproject.model.enums.PaymentType;
import org.zetaproject.model.enums.PaymentCategory;
import org.zetaproject.model.enums.PaymentStatus;

import java.util.Date;

public class Payment {
    private int id;
    private double amount;
    private PaymentType type;
    private PaymentCategory category;
    private PaymentStatus status;
    private Date createdAt;
    private Date updatedAt;
    private int userId;
    private String remarks;

    public Payment() {}

    public Payment(int id, double amount, PaymentType type, PaymentCategory category,
                   PaymentStatus status, Date createdAt, Date updatedAt, int userId, String remarks) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
        this.remarks = remarks;
    }

    // Getters and setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public PaymentType getType() { return type; }
    public void setType(PaymentType type) { this.type = type; }

    public PaymentCategory getCategory() { return category; }
    public void setCategory(PaymentCategory category) { this.category = category; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
