package org.zetaproject.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

public class ReportExpoter {

    public static void exportReportToCSV(List<Map<String, Object>> reportData, String filePath) throws IOException {
        if (reportData == null || reportData.isEmpty()) {
            System.out.println("No data to export.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            Set<String> headers = reportData.get(0).keySet();
            writer.write(String.join(",", headers));
            writer.newLine();

            for (Map<String, Object> row : reportData) {
                List<String> values = new ArrayList<>();
                for (String header : headers) {
                    Object val = row.get(header);
                    String cell = val != null ? val.toString().replace(",", " ") : "";
                    values.add(cell);
                }
                writer.write(String.join(",", values));
                writer.newLine();
            }

            System.out.println("Report exported successfully to: " + filePath);
        }
    }
}
