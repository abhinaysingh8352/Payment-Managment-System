package org.zetaproject.controller;

import org.zetaproject.model.entites.User;
import org.zetaproject.model.enums.UserRole;
import org.zetaproject.services.UserService;

import java.util.Scanner;

public class AuthController {
    private final UserService userService;
    private final Scanner sc;
    private User loggedIn;

    public AuthController(UserService userService, Scanner sc) {
        this.userService = userService;
        this.sc = sc;
    }

    public User promptLogin() {
        while (loggedIn == null) {
            System.out.println("\n=== Auth Menu ===");
            System.out.println("1. Login  2. Register  3. Exit");
            switch (sc.nextLine().trim()) {
                case "1" -> login();
                case "2" -> register();
                case "3" -> System.exit(0);
                default  -> System.out.println("Invalid choice");
            }
        }
        return loggedIn;
    }

    /* ---------- helpers ---------- */

    private void login() {
        try {
            System.out.print("Username: ");
            String u = sc.nextLine().trim();
            System.out.print("Password: ");
            String p = sc.nextLine().trim();

            User user = userService.getUserByUsername(u);
            if (user != null && user.getPassword().equals(p)) {
                loggedIn = user;
            } else System.out.println("❌  Wrong credentials");
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
        }
    }

    private void register() {
        try {
            System.out.print("New username: ");
            String u = sc.nextLine().trim();
            if (userService.getUserByUsername(u) != null) {
                System.out.println("Username taken."); return;
            }
            System.out.print("New password: "); String p = sc.nextLine().trim();
            System.out.print("Role (ADMIN / FINANCE_MANAGER / VIEWER): ");
            UserRole role = UserRole.valueOf(sc.nextLine().trim().toUpperCase());

            userService.addUser(new User(0, u, p, role));
            System.out.println("✅  User created.");
        } catch (Exception e) {
            System.out.println("Registration error: " + e.getMessage());
        }
    }
}
