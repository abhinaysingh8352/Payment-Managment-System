package org.zetaproject.controller;

import org.zetaproject.model.entites.Payment;
import org.zetaproject.model.entites.User;
import org.zetaproject.model.enums.PaymentCategory;
import org.zetaproject.model.enums.PaymentStatus;
import org.zetaproject.model.enums.PaymentType;
import org.zetaproject.model.enums.UserRole;
import org.zetaproject.services.PaymentService;
import org.zetaproject.exceptions.BusinessException;
import org.zetaproject.exceptions.NotFoundException;

import java.util.Scanner;

public class PaymentController {
    private final PaymentService paymentService;
    private final User loggedInUser;
    private final Scanner scanner;

    public PaymentController(PaymentService paymentService, User loggedInUser, Scanner scanner) {
        this.paymentService = paymentService;
        this.loggedInUser = loggedInUser;
        this.scanner = scanner;
    }

    public void addPayment() {
        // Check permissions - Only Admin and Finance Manager can add payments
        if (loggedInUser.getRole() == UserRole.VIEWER) {
            System.out.println("❌ Access denied. Viewers cannot add payments.");
            return;
        }

        try {
            Payment payment = new Payment();

            System.out.print("Enter amount: ");
            payment.setAmount(getDoubleInput());

            System.out.println("\nPayment Type:");
            System.out.println("1. INCOMING");
            System.out.println("2. OUTGOING");
            System.out.print("Choose type (1-2): ");
            int typeChoice = getIntInput();
            payment.setType(typeChoice == 1 ? PaymentType.INCOMING : PaymentType.OUTGOING);

            System.out.println("\nPayment Category:");
            System.out.println("1. SALARY");
            System.out.println("2. VENDOR_PAYMENT");
            System.out.println("3. CLIENT_INVOICE");
            System.out.print("Choose category (1-3): ");
            int categoryChoice = getIntInput();

            PaymentCategory category;
            switch (categoryChoice) {
                case 1: category = PaymentCategory.SALARY; break;
                case 2: category = PaymentCategory.VENDOR_PAYMENT; break;
                case 3: category = PaymentCategory.CLIENT_INVOICE; break;
                default:
                    System.out.println("Invalid choice. Defaulting to CLIENT_INVOICE.");
                    category = PaymentCategory.CLIENT_INVOICE;
            }
            payment.setCategory(category);

            payment.setStatus(PaymentStatus.PENDING);
            payment.setUserId(loggedInUser.getId());

            System.out.print("Enter remarks (optional): ");
            String remarks = scanner.nextLine().trim();
            payment.setRemarks(remarks.isEmpty() ? "Added by " + loggedInUser.getUsername() : remarks);

            paymentService.addPayment(payment);
            System.out.println("✅ Payment added successfully!");

        } catch (BusinessException be) {
            System.out.println("❌ Validation error: " + be.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error adding payment: " + e.getMessage());
        }
    }

    public void updatePaymentStatus() {
        // Check permissions - Only Admin and Finance Manager can update status
        if (loggedInUser.getRole() == UserRole.VIEWER) {
            System.out.println("❌ Access denied. Viewers cannot update payment status.");
            return;
        }

        try {
            System.out.print("Enter Payment ID to update: ");
            int paymentId = getIntInput();

            // First, show current payment details
            Payment currentPayment = paymentService.getPayment(paymentId);
            if (currentPayment == null) {
                System.out.println("❌ Payment not found with ID: " + paymentId);
                return;
            }

            System.out.println("\nCurrent Payment Details:");
            System.out.printf("ID: %d | Amount: $%.2f | Status: %s%n",
                    currentPayment.getId(), currentPayment.getAmount(), currentPayment.getStatus());

            System.out.println("\nNew Status Options:");
            System.out.println("1. PENDING");
            System.out.println("2. PROCESSING");
            System.out.println("3. COMPLETED");
            System.out.print("Choose new status (1-3): ");

            int statusChoice = getIntInput();
            PaymentStatus newStatus;
            switch (statusChoice) {
                case 1: newStatus = PaymentStatus.PENDING; break;
                case 2: newStatus = PaymentStatus.PROCESSING; break;
                case 3: newStatus = PaymentStatus.COMPLETED; break;
                default:
                    System.out.println("❌ Invalid status choice.");
                    return;
            }

            paymentService.updatePaymentStatus(paymentId, newStatus, loggedInUser.getId());
            System.out.println("✅ Payment status updated successfully!");

        } catch (NotFoundException nf) {
            System.out.println("❌ Error: " + nf.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error updating payment status: " + e.getMessage());
        }
    }

    private int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.print("Invalid input. Please enter a number: ");
            return getIntInput();
        }
    }

    private double getDoubleInput() {
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.print("Invalid input. Please enter a valid amount: ");
            return getDoubleInput();
        }
    }
}
