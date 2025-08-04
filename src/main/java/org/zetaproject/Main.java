package org.zetaproject;

import org.zetaproject.controller.PaymentController;
import org.zetaproject.controller.ReportController;
import org.zetaproject.dao.impl.*;
import org.zetaproject.model.entites.User;
import org.zetaproject.model.enums.UserRole;
import org.zetaproject.services.*;

import java.util.Scanner;

public class Main {
    private static User loggedInUser = null;
    private static final Scanner scanner = new Scanner(System.in);

    // Services - initialized once
    private static final UserService userService = new UserService(new UserDaoImpl());
    private static final PaymentService paymentService = new PaymentService(
            new PaymentDaoImpl(), new AuditTrailDaoImpl());
    private static final ReportService reportService = new ReportService(new PaymentDaoImpl());

    public static void main(String[] args) {
        System.out.println("Welcome to Payment Management System");

        try {
            // Authentication Loop
            while (loggedInUser == null) {
                showAuthMenu();
                int choice = getIntInput();

                switch (choice) {
                    case 1:
                        handleLogin();
                        break;
                    case 2:
                        handleUserRegistration();
                        break;
                    case 3:
                        System.out.println("👋 Goodbye!");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }

            // Create controllers with logged-in user
            PaymentController paymentController = new PaymentController(paymentService, loggedInUser, scanner);
            ReportController reportController = new ReportController(reportService, scanner);

            // Main Application Loop
            while (loggedInUser != null) {
                showMainMenu();
                int choice = getIntInput();

                switch (choice) {
                    case 1:
                        paymentController.addPayment();
                        break;
                    case 2:
                        paymentController.updatePaymentStatus();
                        break;
                    case 3:
                        reportController.showReportsMenu();
                        break;
                    case 4:
                        handleLogout();
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }

        } catch (Exception e) {
            System.out.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static void showAuthMenu() {
        System.out.println("\n=== Authentication Menu ===");
        System.out.println("1. Login");
        System.out.println("2. Create New User");
        System.out.println("3. Exit");
        System.out.print("Choose option: ");
    }

    private static void showMainMenu() {
        System.out.println("\n=== Main Menu === (Logged in as: " +
                loggedInUser.getUsername() + " - " + loggedInUser.getRole() + ")");
        System.out.println("1. Add Payment");
        System.out.println("2. Update Payment Status");
        System.out.println("3. Generate Reports");
        System.out.println("4. Logout");
        System.out.print("Choose option: ");
    }

    private static void handleLogin() {
        try {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine().trim();

            User user = userService.getUserByUsername(username);
            if (user != null && user.getPassword().equals(password)) {
                loggedInUser = user;
                System.out.println("✅ Login successful! Welcome, " + loggedInUser.getUsername());
            } else {
                System.out.println("Invalid username or password.");
            }
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
        }
    }

    private static void handleUserRegistration() {
        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();

            User existingUser = userService.getUserByUsername(username);
            if (existingUser != null) {
                System.out.println("Username already exists. Please choose a different username.");
                return;
            }

            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();

            System.out.println("Available roles:");
            System.out.println("1. ADMIN");
            System.out.println("2. FINANCE_MANAGER");
            System.out.println("3. VIEWER");
            System.out.print("Choose role (1-3): ");

            int roleChoice = getIntInput();
            UserRole role;
            switch (roleChoice) {
                case 1: role = UserRole.ADMIN; break;
                case 2: role = UserRole.FINANCE_MANAGER; break;
                case 3: role = UserRole.VIEWER; break;
                default:
                    System.out.println("⚠️ Invalid role choice. Defaulting to VIEWER.");
                    role = UserRole.VIEWER;
            }

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setRole(role);

            userService.addUser(newUser);
            System.out.println("✅ User created successfully! You can now login.");

        } catch (Exception e) {
            System.out.println("User registration error: " + e.getMessage());
        }
    }

    private static void handleLogout() {
        System.out.println("👋 Logging out... Goodbye, " + loggedInUser.getUsername() + "!");
        loggedInUser = null;
    }

    private static int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.print("Invalid input. Please enter a number: ");
            return getIntInput();
        }
    }
}
