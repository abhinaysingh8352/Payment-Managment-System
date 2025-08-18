package org.zetaproject.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.zetaproject.dao.PaymentDao;
import org.zetaproject.dao.UserDao;
import org.zetaproject.dto.PaymentCreateRequest;
import org.zetaproject.dto.PaymentUpdateRequest;
import org.zetaproject.model.entites.Payment;
import org.zetaproject.model.entites.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zetaproject.model.enums.PaymentCategory;
import org.zetaproject.model.enums.PaymentStatus;
import org.zetaproject.model.enums.PaymentType;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Mock
    private PaymentDao paymentDao;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreatePayment() {
        PaymentCreateRequest request = mock(PaymentCreateRequest.class);
        when(request.getAmount()).thenReturn(BigDecimal.valueOf(1000.0));
        when(request.getPaymentType()).thenReturn(PaymentType.INCOMING);
        when(request.getCategory()).thenReturn(PaymentCategory.VENDOR_PAYMENT);
        when(request.getStatus()).thenReturn(PaymentStatus.COMPLETED);

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(userDao.findByEmail("test@example.com")).thenReturn(user);

        doNothing().when(paymentDao).create(any(Payment.class));

        Payment payment = paymentService.createPayment(request);
        assertNotNull(payment);
        assertNotNull(payment.getDate());
        verify(paymentDao).create(payment);
    }

    @Test
    void testGetAllPayments() {
        Payment payment1 = mock(Payment.class);
        Payment payment2 = mock(Payment.class);
        when(paymentDao.findAll()).thenReturn(List.of(payment1, payment2));

        List<Payment> payments = paymentService.getAllPayments();

        assertEquals(2, payments.size());
        verify(paymentDao).findAll();
    }

    @Test
    public void testGetPaymentById() {
        Payment payment = mock(Payment.class);
        when(paymentDao.findById(1L)).thenReturn(payment);

        Payment result = paymentService.getPaymentById(1L);

        assertEquals(payment, result);
        verify(paymentDao).findById(1L);
    }

    @Test
    public void testUpdatePayment() {
        Payment payment = mock(Payment.class);
        PaymentUpdateRequest request = mock(PaymentUpdateRequest.class);

        when(paymentDao.findById(1L)).thenReturn(payment);
        when(request.getAmount()).thenReturn(BigDecimal.valueOf(200.0));
        when(request.getPaymentType()).thenReturn(PaymentType.INCOMING);
        when(request.getCategory()).thenReturn(PaymentCategory.SALARY);
        when(request.getStatus()).thenReturn(PaymentStatus.COMPLETED);

        Payment result = paymentService.updatePayment(1L, request);

        assertEquals(payment, result);
        verify(payment).setAmount(BigDecimal.valueOf(200.0));
        verify(payment).setPaymentType(PaymentType.INCOMING);
        verify(payment).setCategory(PaymentCategory.SALARY);
        verify(payment).setStatus(PaymentStatus.COMPLETED);
        verify(paymentDao).update(payment);
    }

    @Test
    public void testUpdatePayment_NotFound() {
        when(paymentDao.findById(2L)).thenReturn(null);
        PaymentUpdateRequest request = mock(PaymentUpdateRequest.class);

        Payment result = paymentService.updatePayment(2L, request);

        assertNull(result);
        verify(paymentDao, never()).update(any());
    }

    @Test
    public void testDeletePayment() {
        doNothing().when(paymentDao).delete(1L);

        paymentService.deletePayment(1L);

        verify(paymentDao).delete(1L);
    }
}