package org.zetaproject.controller;

import org.zetaproject.services.ReportService;
import org.zetaproject.utils.ReportExpoter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReportController {

    private final ReportService reportService;
    private final Scanner scanner;

    public ReportController(ReportService reportService, Scanner scanner) {
        this.reportService = reportService;
        this.scanner = scanner;
    }

    public void showReportsMenu() {
        try {
            System.out.println("\n=== Reports Menu ===");
            System.out.println("1. Monthly Report");
            System.out.println("2. Quarterly Report");
            System.out.println("3. Custom Report");
            System.out.println("4. Back to Main Menu");
            System.out.print("Choose option: ");
            int choice = getIntInput();

            switch (choice) {
                case 1 -> generateMonthlyReport();
                case 2 -> generateQuarterlyReport();
                case 3 -> generateCustomReport();
                case 4 -> {
                    return; // Back to main menu
                }
                default -> System.out.println("❌ Invalid choice.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error generating reports: " + e.getMessage());
        }
    }

    private void generateMonthlyReport() {
        try {
            System.out.print("Enter month (1-12): ");
            int month = getIntInput();
            if (month < 1 || month > 12) {
                System.out.println("❌ Invalid month. Please enter a value between 1-12.");
                return;
            }
            System.out.print("Enter year: ");
            int year = getIntInput();

            Map<String, Double> report = reportService.getMonthlyReport(month, year);
            printCategoryReport(report, String.format("MONTHLY REPORT FOR %02d/%d", month, year));

            promptDownload(report);
        } catch (Exception e) {
            System.out.println("❌ Error generating monthly report: " + e.getMessage());
        }
    }

    private void generateQuarterlyReport() {
        try {
            System.out.print("Enter quarter (1-4): ");
            int quarter = getIntInput();
            if (quarter < 1 || quarter > 4) {
                System.out.println("❌ Invalid quarter. Please enter a value between 1-4.");
                return;
            }
            System.out.print("Enter year: ");
            int year = getIntInput();

            Map<String, Double> report = reportService.getQuarterlyReport(quarter, year);
            printCategoryReport(report, String.format("QUARTERLY REPORT FOR Q%d/%d", quarter, year));

            promptDownload(report);
        } catch (Exception e) {
            System.out.println("❌ Error generating quarterly report: " + e.getMessage());
        }
    }

    private void generateCustomReport() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            System.out.print("Enter start date (yyyy-MM-dd): ");
            Date startDate = dateFormat.parse(scanner.nextLine().trim());
            System.out.print("Enter end date (yyyy-MM-dd): ");
            Date endDate = dateFormat.parse(scanner.nextLine().trim());

            if (!startDate.before(endDate)) {
                System.out.println("❌ Start date must be before end date.");
                return;
            }

            List<Map<String, Object>> report = reportService.getCustomReport(startDate, endDate);
            if (report.isEmpty()) {
                System.out.println("📝 No completed payments found for this period.");
                return;
            }

            System.out.println("\nCustom Report from " + dateFormat.format(startDate) + " to " + dateFormat.format(endDate));
            for (Map<String, Object> row : report) {
                System.out.printf("Payment ID: %d | Amount: %.2f | Type: %s | Status: %s | Created At: %s%n",
                        row.get("paymentId"), row.get("amount"), row.get("type"), row.get("status"), row.get("createdAt"));
                System.out.printf("Audit: Action=%s | Timestamp=%s | Details=%s%n%n",
                        row.get("auditAction"), row.get("auditTimestamp"), row.get("auditDetails"));
            }

            promptDownload(report);
        } catch (ParseException e) {
            System.out.println("❌ Invalid date format. Please use yyyy-MM-dd.");
        } catch (Exception e) {
            System.out.println("❌ Error generating custom report: " + e.getMessage());
        }
    }

    private void printCategoryReport(Map<String, Double> report, String title) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("📊 " + title);
        System.out.println("=".repeat(50));
        if (report.isEmpty()) {
            System.out.println("📝 No completed payments found.");
        } else {
            System.out.printf("%-25s %15s%n", "CATEGORY", "AMOUNT");
            System.out.println("-".repeat(42));
            double total = 0;
            for (Map.Entry<String, Double> entry : report.entrySet()) {
                System.out.printf("%-25s $%14.2f%n", entry.getKey(), entry.getValue());
                total += entry.getValue();
            }
            System.out.println("-".repeat(42));
            System.out.printf("%-25s $%14.2f%n", "TOTAL", total);
        }
        System.out.println("=".repeat(50));
    }

    private int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException ex) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    private void promptDownload(Object reportData) {
        System.out.print("Do you want to download this report as CSV? (Y/N): ");
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("Y")) {
            System.out.print("Enter file name to save (e.g., report.csv): ");
            String fileName = scanner.nextLine().trim();
            try {
                if (reportData instanceof Map) {
                    // Convert Map<String, Double> to List<Map<String,Object>>
                    List<Map<String, Object>> listData = new ArrayList<>();
                    Map<String, Double> mapData = (Map<String, Double>) reportData;
                    for (Map.Entry<String, Double> entry : mapData.entrySet()) {
                        Map<String, Object> row = new HashMap<>();
                        row.put("Category", entry.getKey());
                        row.put("Amount", entry.getValue());
                        listData.add(row);
                    }
                    org.zetaproject.utils.ReportExpoter.exportReportToCSV(listData, fileName);
                } else if (reportData instanceof List) {
                    org.zetaproject.utils.ReportExpoter.exportReportToCSV((List<Map<String, Object>>) reportData, fileName);
                } else {
                    System.out.println("Unsupported report data type for export.");
                }
            } catch (Exception e) {
                System.out.println("❌ Error exporting report: " + e.getMessage());
            }
        }
    }
}
