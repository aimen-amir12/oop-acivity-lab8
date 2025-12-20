package university.modules;

import university.database.DatabaseConnection;
import java.sql.*;
import java.util.Scanner;

public class AdminManager {
    private static Scanner scanner = new Scanner(System.in);

    public static void showMenu() {
        while (true) {
            System.out.println("\n=== ADMIN PANEL ===");
            System.out.println("1. System Statistics");
            System.out.println("2. Database Backup");
            System.out.println("3. Generate Reports");
            System.out.println("4. Manage All Users");
            System.out.println("5. View System Logs");
            System.out.println("6. Reset Database");
            System.out.println("7. Back to Main Menu");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: showSystemStatistics(); break;
                case 2: backupDatabase(); break;
                case 3: generateReports(); break;
                case 4: manageAllUsers(); break;
                case 5: viewSystemLogs(); break;
                case 6: resetDatabase(); break;
                case 7: return;
                default: System.out.println("Invalid choice!");
            }
        }
    }

    private static void showSystemStatistics() {
        System.out.println("\n=== SYSTEM STATISTICS ===");

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Count students
            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) FROM students");
            rs1.next();
            int studentCount = rs1.getInt(1);

            // Count faculty
            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) FROM faculty");
            rs2.next();
            int facultyCount = rs2.getInt(1);

            // Count courses
            ResultSet rs3 = stmt.executeQuery("SELECT COUNT(*) FROM courses");
            rs3.next();
            int courseCount = rs3.getInt(1);

            // Count books
            ResultSet rs4 = stmt.executeQuery("SELECT COUNT(*) FROM library_books");
            rs4.next();
            int bookCount = rs4.getInt(1);

            // Active enrollments
            ResultSet rs5 = stmt.executeQuery("SELECT COUNT(*) FROM enrollments WHERE status = 'ENROLLED'");
            rs5.next();
            int activeEnrollments = rs5.getInt(1);

            System.out.println("Total Students: " + studentCount);
            System.out.println("Total Faculty: " + facultyCount);
            System.out.println("Total Courses: " + courseCount);
            System.out.println("Total Books: " + bookCount);
            System.out.println("Active Enrollments: " + activeEnrollments);

            // Revenue summary
            ResultSet rs6 = stmt.executeQuery("SELECT SUM(amount) FROM payments WHERE payment_type = 'TUITION_PAYMENT'");
            rs6.next();
            double tuitionRevenue = rs6.getDouble(1);

            System.out.println("Tuition Revenue: Rs. " + (tuitionRevenue > 0 ? tuitionRevenue : 0));

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void backupDatabase() {
        System.out.println("\n=== DATABASE BACKUP ===");
        System.out.println("This would export all tables to SQL file.");
        System.out.println("Backup feature requires MySQL dump utility.");
        System.out.println("Backup simulation completed!");
    }

    private static void generateReports() {
        while (true) {
            System.out.println("\n=== GENERATE REPORTS ===");
            System.out.println("1. Student Transcripts");
            System.out.println("2. Faculty Teaching Load");
            System.out.println("3. Course Enrollment Report");
            System.out.println("4. Financial Summary");
            System.out.println("5. Library Usage Report");
            System.out.println("6. Back");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: generateStudentTranscripts(); break;
                case 2: generateFacultyTeachingLoad(); break;
                case 3: generateCourseEnrollmentReport(); break;
                case 4: generateFinancialSummary(); break;
                case 5: generateLibraryUsageReport(); break;
                case 6: return;
                default: System.out.println("Invalid choice!");
            }
        }
    }

    private static void generateStudentTranscripts() {
        System.out.println("\n=== STUDENT TRANSCRIPTS ===");
        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();

        String sql = """
            SELECT s.name, c.title, e.grade, c.credits 
            FROM enrollments e 
            JOIN courses c ON e.course_code = c.course_code 
            JOIN students st ON e.student_id = st.student_id 
            JOIN persons s ON st.person_id = s.person_id 
            WHERE e.student_id = ? AND e.grade IS NOT NULL
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n=== ACADEMIC TRANSCRIPT ===");
            System.out.println("Student ID: " + studentId);
            System.out.println("Course\t\t\tGrade\tCredits");
            System.out.println("----------------------------------");

            double totalPoints = 0;
            int totalCredits = 0;
            boolean found = false;

            while (rs.next()) {
                found = true;
                double grade = rs.getDouble("grade");
                int credits = rs.getInt("credits");
                totalPoints += (grade * credits);
                totalCredits += credits;

                System.out.printf("%-15s\t%.2f\t%d\n",
                        rs.getString("title"),
                        grade,
                        credits);
            }

            if (found) {
                double gpa = totalCredits > 0 ? totalPoints / totalCredits : 0;
                System.out.println("----------------------------------");
                System.out.printf("GPA: %.2f\n", gpa);
                System.out.println("Total Credits: " + totalCredits);
            } else {
                System.out.println("No completed courses found!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void generateFacultyTeachingLoad() {
        System.out.println("\n=== FACULTY TEACHING LOAD ===");

        String sql = """
            SELECT p.name, f.faculty_id, 
                   COUNT(DISTINCT e.course_code) as courses_taught,
                   COUNT(e.student_id) as total_students
            FROM faculty f 
            JOIN persons p ON f.person_id = p.person_id 
            LEFT JOIN enrollments e ON e.course_code IN (
                SELECT course_code FROM courses WHERE course_code LIKE 'CS%'
            )
            GROUP BY f.faculty_id, p.name
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("Faculty\t\t\tID\tCourses\tStudents");
            System.out.println("------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-15s\t%s\t%d\t%d\n",
                        rs.getString("name"),
                        rs.getString("faculty_id"),
                        rs.getInt("courses_taught"),
                        rs.getInt("total_students"));
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void manageAllUsers() {
        System.out.println("\n=== MANAGE ALL USERS ===");

        String sql = "SELECT * FROM persons ORDER BY person_type, person_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("ID\tName\t\t\tType\t\tSubtype");
            System.out.println("------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-8s\t%-15s\t%-10s\t%s\n",
                        rs.getString("person_id"),
                        rs.getString("name"),
                        rs.getString("person_type"),
                        rs.getString("person_subtype"));
            }

            System.out.println("\nOptions: [D]elete User, [E]dit User, [B]ack");
            System.out.print("Enter option: ");
            String option = scanner.nextLine().toUpperCase();

            if (option.equals("D")) {
                System.out.print("Enter User ID to delete: ");
                String userId = scanner.nextLine();

                String deleteSql = "DELETE FROM persons WHERE person_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                    pstmt.setString(1, userId);
                    int rows = pstmt.executeUpdate();
                    System.out.println(rows > 0 ? "User deleted!" : "User not found!");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewSystemLogs() {
        System.out.println("\n=== SYSTEM LOGS ===");
        System.out.println("Recent Activities:");
        System.out.println("1. Database initialized - " + java.time.LocalDateTime.now());
        System.out.println("2. All modules loaded successfully");
        System.out.println("3. Connection pool established");
        System.out.println("\n(Logs would be stored in a log file in production)");
    }

    private static void resetDatabase() {
        System.out.println("\n=== RESET DATABASE ===");
        System.out.print("WARNING: This will delete all data! Type 'CONFIRM' to proceed: ");
        String confirm = scanner.nextLine();

        if (!confirm.equals("CONFIRM")) {
            System.out.println("Reset cancelled.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Drop all tables in correct order
            String[] tables = {
                    "library_transactions", "library_books", "payments",
                    "department_courses", "department_faculty", "enrollments",
                    "students", "faculty", "staff", "courses", "departments", "persons"
            };

            for (String table : tables) {
                try {
                    stmt.executeUpdate("DROP TABLE IF EXISTS " + table);
                } catch (SQLException e) {
                    // Ignore if table doesn't exist
                }
            }

            System.out.println("Database reset complete!");
            System.out.println("Please restart the application to reinitialize tables.");

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void generateCourseEnrollmentReport() {
        System.out.println("\n=== COURSE ENROLLMENT REPORT ===");

        String sql = """
            SELECT c.course_code, c.title, c.capacity, c.current_enrollment,
                   (c.current_enrollment * 100.0 / c.capacity) as percentage_full
            FROM courses c
            ORDER BY percentage_full DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("Course\tTitle\t\t\tCapacity\tEnrolled\t% Full");
            System.out.println("------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-6s\t%-20s\t%-8d\t%-9d\t%.1f%%\n",
                        rs.getString("course_code"),
                        rs.getString("title"),
                        rs.getInt("capacity"),
                        rs.getInt("current_enrollment"),
                        rs.getDouble("percentage_full"));
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void generateFinancialSummary() {
        System.out.println("\n=== FINANCIAL SUMMARY ===");

        String sql = """
            SELECT 
                SUM(CASE WHEN payment_type = 'TUITION_PAYMENT' THEN amount ELSE 0 END) as tuition_revenue,
                SUM(CASE WHEN payment_type = 'SALARY_PAYMENT' THEN amount ELSE 0 END) as salary_expenses,
                COUNT(*) as total_transactions
            FROM payments
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                double revenue = rs.getDouble("tuition_revenue");
                double expenses = rs.getDouble("salary_expenses");
                double net = revenue - expenses;

                System.out.println("Tuition Revenue: Rs. " + revenue);
                System.out.println("Salary Expenses: Rs. " + expenses);
                System.out.println("Net Balance: Rs. " + net);
                System.out.println("Total Transactions: " + rs.getInt("total_transactions"));
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void generateLibraryUsageReport() {
        System.out.println("\n=== LIBRARY USAGE REPORT ===");

        String sql = """
            SELECT 
                COUNT(*) as total_books,
                SUM(CASE WHEN is_borrowed = TRUE THEN 1 ELSE 0 END) as borrowed_books,
                (SELECT COUNT(*) FROM library_transactions WHERE MONTH(borrow_date) = MONTH(CURDATE())) as this_month_borrowals
            FROM library_books
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int total = rs.getInt("total_books");
                int borrowed = rs.getInt("borrowed_books");
                int thisMonth = rs.getInt("this_month_borrowals");

                System.out.println("Total Books: " + total);
                System.out.println("Currently Borrowed: " + borrowed);
                System.out.println("Available: " + (total - borrowed));
                System.out.println("Borrowals This Month: " + thisMonth);
                System.out.println("Utilization Rate: " + (borrowed * 100.0 / total) + "%");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}