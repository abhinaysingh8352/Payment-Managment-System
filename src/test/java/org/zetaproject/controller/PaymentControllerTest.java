package org.zetaproject.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zetaproject.dto.PaymentCreateRequest;
import org.zetaproject.dto.PaymentResponse;
import org.zetaproject.dto.PaymentUpdateRequest;
import org.zetaproject.model.entites.Payment;
import org.zetaproject.model.enums.PaymentCategory;
import org.zetaproject.model.enums.PaymentStatus;
import org.zetaproject.model.enums.PaymentType;
import org.zetaproject.services.PaymentService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private Payment payment1;
    private Payment payment2;

    @BeforeEach
    void setUp() {
        payment1 = new Payment();
        payment1.setId(1L);
        payment1.setAmount(BigDecimal.valueOf(100.50));
        payment1.setPaymentType(PaymentType.INCOMING);
        payment1.setCategory(PaymentCategory.SALARY);
        payment1.setStatus(PaymentStatus.PENDING);
        payment1.setDate(LocalDateTime.now());
        payment1.setCreatedBy(1L);

        payment2 = new Payment();
        payment2.setId(2L);
        payment2.setAmount(BigDecimal.valueOf(250.75));
        payment2.setPaymentType(PaymentType.OUTGOING);
        payment2.setCategory(PaymentCategory.SALARY);
        payment2.setStatus(PaymentStatus.COMPLETED);
        payment2.setDate(LocalDateTime.now());
        payment2.setCreatedBy(2L);
    }

    @Test
    public void testCreatePayment() {
        PaymentCreateRequest request = new PaymentCreateRequest(
                BigDecimal.valueOf(5000.00),
                PaymentType.INCOMING,
                PaymentCategory.SALARY,
                PaymentStatus.COMPLETED
        );

        when(paymentService.createPayment(request)).thenReturn(payment1);

        PaymentResponse result = paymentController.createPayment(request);

        assertThat(result.getId()).isEqualTo(payment1.getId());
        assertThat(result.getAmount()).isEqualTo(payment1.getAmount());
        verify(paymentService, times(1)).createPayment(request);
    }

    @Test
    public void testGetAllPayments() {
        when(paymentService.getAllPayments()).thenReturn(Arrays.asList(payment1, payment2));

        List<PaymentResponse> results = paymentController.getAllPayments();

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getId()).isEqualTo(payment1.getId());
        assertThat(results.get(1).getId()).isEqualTo(payment2.getId());
        verify(paymentService, times(1)).getAllPayments();
    }

    @Test
    public void testGetPaymentById() {
        when(paymentService.getPaymentById(1L)).thenReturn(payment1);

        PaymentResponse result = paymentController.getPaymentById(1L);

        assertThat(result.getId()).isEqualTo(payment1.getId());
        assertThat(result.getStatus()).isEqualTo(payment1.getStatus());
        verify(paymentService, times(1)).getPaymentById(1L);
    }

    @Test
    public void testUpdatePayment() {
        PaymentUpdateRequest updateRequest = new PaymentUpdateRequest(
                BigDecimal.valueOf(200.00),
                PaymentType.OUTGOING,
                PaymentCategory.VENDOR_PAYMENT,
                PaymentStatus.PENDING
        );

        when(paymentService.updatePayment(1L, updateRequest)).thenReturn(payment2);

        PaymentResponse result = paymentController.updatePayment(1L, updateRequest);

        assertThat(result.getId()).isEqualTo(payment2.getId());
        assertThat(result.getAmount()).isEqualTo(payment2.getAmount());
        verify(paymentService, times(1)).updatePayment(1L, updateRequest);
    }

    @Test
    public void testDeletePayment() {
        doNothing().when(paymentService).deletePayment(1L);

        paymentController.deletePayment(1L);

        verify(paymentService, times(1)).deletePayment(1L);
    }
}
