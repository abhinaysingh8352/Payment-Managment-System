package org.zetaproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.zetaproject.dao.PaymentDao;
import org.zetaproject.dao.UserDao;
import org.zetaproject.dto.PaymentCreateRequest;
import org.zetaproject.dto.PaymentUpdateRequest;
import org.zetaproject.model.entites.Payment;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentDao paymentDao;

    @Autowired
    private UserDao userDao;

    private Long extractUserIdFromContext() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            var user = userDao.findByEmail(email);
            return user != null ? user.getId() : null;
        }
        return null;
    }

    public Payment createPayment(PaymentCreateRequest request) {
        Payment payment = new Payment();
        payment.setAmount(request.getAmount());
        payment.setPaymentType(request.getPaymentType());
        payment.setCategory(request.getCategory());
        payment.setStatus(request.getStatus());
        payment.setDate(LocalDateTime.now());
        Long userId = extractUserIdFromContext();
        payment.setCreatedBy(userId);
        paymentDao.create(payment);
        return payment;
    }

    public List<Payment> getAllPayments() {
        return paymentDao.findAll();
    }

    public Payment getPaymentById(Long id) {
        return paymentDao.findById(id);
    }

    public Payment updatePayment(Long id, PaymentUpdateRequest request) {
        Payment payment = paymentDao.findById(id);
        if (payment != null) {
            payment.setAmount(request.getAmount());
            payment.setPaymentType(request.getPaymentType());
            payment.setCategory(request.getCategory());
            payment.setStatus(request.getStatus());
            paymentDao.update(payment);
        }
        return payment;
    }

    public void deletePayment(Long id) {
        paymentDao.delete(id);
    }
}