package org.zetaproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zetaproject.dto.PaymentCreateRequest;
import org.zetaproject.dto.PaymentResponse;
import org.zetaproject.dto.PaymentUpdateRequest;
import org.zetaproject.model.entites.Payment;
import org.zetaproject.services.PaymentService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public PaymentResponse createPayment(@RequestBody PaymentCreateRequest request) {
        Payment createdPayment = paymentService.createPayment(request);
        return new PaymentResponse(createdPayment.getId(), createdPayment.getAmount(), createdPayment.getPaymentType(), createdPayment.getCategory(), createdPayment.getStatus(), createdPayment.getDate(), createdPayment.getCreatedBy());
    }

    @GetMapping
    public List<PaymentResponse> getAllPayments() {
        return paymentService.getAllPayments().stream()
                .map(p -> new PaymentResponse(p.getId(), p.getAmount(), p.getPaymentType(), p.getCategory(), p.getStatus(), p.getDate(), p.getCreatedBy()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PaymentResponse getPaymentById(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);
        return new PaymentResponse(payment.getId(), payment.getAmount(), payment.getPaymentType(), payment.getCategory(), payment.getStatus(), payment.getDate(), payment.getCreatedBy());
    }

    @PutMapping("/{id}")
    public PaymentResponse updatePayment(@PathVariable Long id, @RequestBody PaymentUpdateRequest request) {
        Payment updatedPayment = paymentService.updatePayment(id, request);
        return new PaymentResponse(updatedPayment.getId(), updatedPayment.getAmount(), updatedPayment.getPaymentType(), updatedPayment.getCategory(), updatedPayment.getStatus(), updatedPayment.getDate(), updatedPayment.getCreatedBy());
    }

    @DeleteMapping("/{id}")
    public void deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
    }
}