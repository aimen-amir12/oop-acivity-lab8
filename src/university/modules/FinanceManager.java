package university.modules;

import university.database.DatabaseConnection;
import university.database.SQLQueries;
import java.sql.*;
import java.util.Scanner;

public class FinanceManager {
    private static Scanner scanner = new Scanner(System.in);

    public static void showMenu() {
        while (true) {
            System.out.println("\n=== FINANCE MANAGEMENT ===");
            System.out.println("1. Record Tuition Payment");
            System.out.println("2. Record Salary Payment");
            System.out.println("3. View All Payments");
            System.out.println("4. View Student Tuition Status");
            System.out.println("5. View Faculty Salary Status");
            System.out.println("6. Generate Financial Report");
            System.out.println("7. Back to Main Menu");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: recordTuitionPayment(); break;
                case 2: recordSalaryPayment(); break;
                case 3: viewAllPayments(); break;
                case 4: viewStudentTuitionStatus(); break;
                case 5: viewFacultySalaryStatus(); break;
                case 6: generateFinancialReport(); break;
                case 7: return;
                default: System.out.println("Invalid choice!");
            }
        }
    }

    private static void recordTuitionPayment() {
        System.out.println("\n=== RECORD TUITION PAYMENT ===");

        System.out.print("Student ID: ");
        String studentId = scanner.nextLine();

        System.out.print("Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Description: ");
        String description = scanner.nextLine();

        String sql = SQLQueries.INSERT_PAYMENT;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            pstmt.setString(2, "UNIVERSITY");
            pstmt.setDouble(3, amount);
            pstmt.setString(4, "TUITION_PAYMENT");
            pstmt.setString(5, description);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Tuition payment recorded!");

                // Update student balance
                updateStudentBalance(studentId, -amount);
            } else {
                System.out.println("Payment failed!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void recordSalaryPayment() {
        System.out.println("\n=== RECORD SALARY PAYMENT ===");

        System.out.print("Faculty/Staff ID: ");
        String personId = scanner.nextLine();

        System.out.print("Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Description: ");
        String description = scanner.nextLine();

        String sql = SQLQueries.INSERT_PAYMENT;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "UNIVERSITY");
            pstmt.setString(2, personId);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, "SALARY_PAYMENT");
            pstmt.setString(5, description);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Salary payment recorded!");

                // Update faculty/staff balance
                updateFacultyBalance(personId, amount);
            } else {
                System.out.println("Payment failed!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void updateStudentBalance(String studentId, double amount) {
        String sql = "UPDATE students SET total_balance = total_balance + ? WHERE student_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, amount);
            pstmt.setString(2, studentId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error updating balance: " + e.getMessage());
        }
    }

    private static void updateFacultyBalance(String facultyId, double amount) {
        String sql = "UPDATE faculty SET total_balance = total_balance + ? WHERE faculty_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, amount);
            pstmt.setString(2, facultyId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error updating balance: " + e.getMessage());
        }
    }

    private static void viewAllPayments() {
        System.out.println("\n=== ALL PAYMENTS ===");
        System.out.println("Date\t\tPayer\t\tPayee\t\tAmount\tType\tDescription");
        System.out.println("-------------------------------------------------------------------------------");

        String sql = """
            SELECT p.*, 
                   payer.name as payer_name, 
                   payee.name as payee_name 
            FROM payments p 
            LEFT JOIN persons payer ON p.payer_id = payer.person_id 
            LEFT JOIN persons payee ON p.payee_id = payee.person_id 
            ORDER BY p.payment_date DESC 
            LIMIT 50
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.printf("%s\t%-10s\t%-10s\t%.2f\t%-12s\t%s\n",
                        rs.getTimestamp("payment_date").toString().substring(0, 10),
                        rs.getString("payer_name") != null ? rs.getString("payer_name") : rs.getString("payer_id"),
                        rs.getString("payee_name") != null ? rs.getString("payee_name") : rs.getString("payee_id"),
                        rs.getDouble("amount"),
                        rs.getString("payment_type"),
                        rs.getString("description"));
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewStudentTuitionStatus() {
        System.out.println("\n=== STUDENT TUITION STATUS ===");
        System.out.println("Student ID\tName\t\tTotal Balance");
        System.out.println("--------------------------------------------");

        String sql = """
            SELECT s.student_id, p.name, s.total_balance 
            FROM students s 
            JOIN persons p ON s.person_id = p.person_id 
            WHERE s.total_balance != 0 
            ORDER BY s.total_balance DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            boolean found = false;
            while (rs.next()) {
                found = true;
                double balance = rs.getDouble("total_balance");
                String status = balance > 0 ? "OWES" : "CREDIT";

                System.out.printf("%-12s\t%-15s\t%.2f (%s)\n",
                        rs.getString("student_id"),
                        rs.getString("name"),
                        Math.abs(balance),
                        status);
            }

            if (!found) {
                System.out.println("All students have zero balance!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewFacultySalaryStatus() {
        System.out.println("\n=== FACULTY SALARY STATUS ===");
        System.out.println("Faculty ID\tName\t\tSalary\tBalance Received");
        System.out.println("-----------------------------------------------------");

        String sql = """
            SELECT f.faculty_id, p.name, f.salary, f.total_balance 
            FROM faculty f 
            JOIN persons p ON f.person_id = p.person_id 
            ORDER BY f.salary DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.printf("%-12s\t%-15s\t%.2f\t%.2f\n",
                        rs.getString("faculty_id"),
                        rs.getString("name"),
                        rs.getDouble("salary"),
                        rs.getDouble("total_balance"));
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void generateFinancialReport() {
        System.out.println("\n=== FINANCIAL REPORT ===");

        String sql = """
            SELECT 
                SUM(CASE WHEN payment_type = 'TUITION_PAYMENT' THEN amount ELSE 0 END) as tuition_revenue,
                SUM(CASE WHEN payment_type = 'SALARY_PAYMENT' THEN amount ELSE 0 END) as salary_expenses,
                COUNT(DISTINCT CASE WHEN payment_type = 'TUITION_PAYMENT' THEN payer_id END) as paying_students,
                COUNT(DISTINCT CASE WHEN payment_type = 'SALARY_PAYMENT' THEN payee_id END) as paid_employees,
                MIN(payment_date) as first_payment,
                MAX(payment_date) as last_payment
            FROM payments
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                double revenue = rs.getDouble("tuition_revenue");
                double expenses = rs.getDouble("salary_expenses");
                double net = revenue - expenses;

                System.out.println("=== SUMMARY ===");
                System.out.printf("Total Tuition Revenue: Rs. %.2f\n", revenue);
                System.out.printf("Total Salary Expenses: Rs. %.2f\n", expenses);
                System.out.printf("Net Balance: Rs. %.2f\n", net);
                System.out.println("Paying Students: " + rs.getInt("paying_students"));
                System.out.println("Paid Employees: " + rs.getInt("paid_employees"));
                System.out.println("First Payment: " + (rs.getTimestamp("first_payment") != null ?
                        rs.getTimestamp("first_payment").toString().substring(0, 10) : "N/A"));
                System.out.println("Last Payment: " + (rs.getTimestamp("last_payment") != null ?
                        rs.getTimestamp("last_payment").toString().substring(0, 10) : "N/A"));

                // Monthly breakdown
                System.out.println("\n=== MONTHLY BREAKDOWN ===");
                String monthlySql = """
                    SELECT 
                        DATE_FORMAT(payment_date, '%Y-%m') as month,
                        SUM(CASE WHEN payment_type = 'TUITION_PAYMENT' THEN amount ELSE 0 END) as tuition,
                        SUM(CASE WHEN payment_type = 'SALARY_PAYMENT' THEN amount ELSE 0 END) as salary,
                        COUNT(*) as transactions
                    FROM payments
                    GROUP BY DATE_FORMAT(payment_date, '%Y-%m')
                    ORDER BY month DESC
                    LIMIT 6
                    """;

                try (Statement stmt2 = conn.createStatement();
                     ResultSet rs2 = stmt2.executeQuery(monthlySql)) {

                    System.out.println("Month\t\tTuition\t\tSalary\t\tNet\tTransactions");
                    System.out.println("--------------------------------------------------------------");

                    while (rs2.next()) {
                        double tuition = rs2.getDouble("tuition");
                        double salary = rs2.getDouble("salary");
                        double netMonthly = tuition - salary;

                        System.out.printf("%s\t%.2f\t\t%.2f\t\t%.2f\t%d\n",
                                rs2.getString("month"),
                                tuition,
                                salary,
                                netMonthly,
                                rs2.getInt("transactions"));
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}