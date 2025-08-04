package org.zetaproject.dao;

import org.zetaproject.model.entites.Payment;
import java.util.List;

public interface PaymentDao {
    void addPayment(Payment payment) throws Exception;
    void updatePaymentStatus(int paymentId, String status) throws Exception;
    Payment getPayment(int paymentId) throws Exception;
    List<Payment> getAllPayments() throws Exception;
    List<Payment> getPaymentsByUser(int userId) throws Exception;
}
