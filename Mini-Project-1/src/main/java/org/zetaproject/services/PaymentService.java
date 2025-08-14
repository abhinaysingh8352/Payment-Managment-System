package org.zetaproject.services;

import org.zetaproject.dao.PaymentDao;
import org.zetaproject.dao.AuditTrailDao;
import org.zetaproject.model.entites.Payment;
import org.zetaproject.model.entites.AuditTrail;
import org.zetaproject.model.enums.PaymentStatus;
import org.zetaproject.exceptions.BusinessException;
import org.zetaproject.exceptions.NotFoundException;

import java.util.Date;
import java.util.List;

public class PaymentService {
    private PaymentDao paymentDao;
    private AuditTrailDao auditTrailDao;

    public PaymentService(PaymentDao paymentDao, AuditTrailDao auditTrailDao) {
        this.paymentDao = paymentDao;
        this.auditTrailDao = auditTrailDao;
    }

    public void addPayment(Payment payment) throws BusinessException, Exception {
        if (payment.getAmount() <= 0)
            throw new BusinessException("Amount must be positive.");
        // More validations here...
        payment.setCreatedAt(new Date());
        payment.setUpdatedAt(new Date());
        paymentDao.addPayment(payment);
        logAudit("Add Payment", payment.getUserId(), payment.getId(), "Payment added.");
    }

    public void updatePaymentStatus(int paymentId, PaymentStatus newStatus, int userId) throws Exception {
        Payment payment = paymentDao.getPayment(paymentId);
        if (payment == null)
            throw new NotFoundException("Payment not found.");
        paymentDao.updatePaymentStatus(paymentId, newStatus.name());
        logAudit("Update Payment Status", userId, paymentId, "Status changed to: " + newStatus);
    }

    public Payment getPayment(int paymentId) throws Exception {
        return paymentDao.getPayment(paymentId);
    }

    public List<Payment> getAllPayments() throws Exception {
        return paymentDao.getAllPayments();
    }

    public List<Payment> getPaymentsByUser(int userId) throws Exception {
        return paymentDao.getPaymentsByUser(userId);
    }

    private void logAudit(String action, int userId, int paymentId, String details) throws Exception {
        AuditTrail audit = new AuditTrail();
        audit.setAction(action);
        audit.setUserId(userId);
        audit.setPaymentId(paymentId);
        audit.setTimestamp(new Date());
        audit.setDetails(details);
        auditTrailDao.logAction(audit);
    }
}
