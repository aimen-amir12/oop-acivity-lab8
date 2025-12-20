package university.modules;

import university.people.*;
import university.database.DatabaseConnection;
import university.database.SQLQueries;
import java.sql.*;
import java.util.Scanner;

public class StudentManager {
    private static Scanner scanner = new Scanner(System.in);

    public static void showMenu() {
        while (true) {
            System.out.println("\n=== STUDENT MANAGEMENT ===");
            System.out.println("1. Add New Student");
            System.out.println("2. View All Students");
            System.out.println("3. Search Student");
            System.out.println("4. Update Student GPA");
            System.out.println("5. Delete Student");
            System.out.println("6. Enroll Student in Course");
            System.out.println("7. View Student Schedule");
            System.out.println("8. View Student Payments");
            System.out.println("9. Back to Main Menu");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: addStudent(); break;
                case 2: viewAllStudents(); break;
                case 3: searchStudent(); break;
                case 4: updateStudentGPA(); break;
                case 5: deleteStudent(); break;
                case 6: enrollInCourse(); break;
                case 7: viewStudentSchedule(); break;
                case 8: viewStudentPayments(); break;
                case 9: return;
                default: System.out.println("Invalid choice!");
            }
        }
    }

    private static void addStudent() {
        System.out.println("\n=== ADD NEW STUDENT ===");

        System.out.print("Student ID: ");
        String id = scanner.nextLine();

        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Phone: ");
        String phone = scanner.nextLine();

        System.out.print("Student Type (1. Undergraduate, 2. Graduate, 3. PhD): ");
        int type = scanner.nextInt();
        scanner.nextLine();

        Student student ;

        switch (type) {
            case 1:
                student = new UndergraduateStudent(id, name, email, phone);
                break;
            case 2:
                System.out.print("Advisor: ");
                String advisor = scanner.nextLine();
                student = new GraduateStudent(id, name, email, phone, advisor);
                break;
            case 3:
                student = new PhDStudent(id, name, email, phone);
                break;
            default:
                System.out.println("Invalid type!");
                return;
        }

        if (student.saveToDatabase()) {
            System.out.println("Student added successfully!");
            student.register();
        } else {
            System.out.println("Failed to add student!");
        }
    }

    private static void viewAllStudents() {
        System.out.println("\n=== ALL STUDENTS ===");
        System.out.println("ID\tName\t\tEmail\t\t\tGPA\tBalance");
        System.out.println("------------------------------------------------------------------------");

        try (ResultSet rs = Student.getAllStudentsFromDatabase()) {
            while (rs.next()) {
                System.out.printf("%-8s %-15s %-20s %-6.2f %-10.2f\n",
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getDouble("gpa"),
                        rs.getDouble("total_balance"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void searchStudent() {
        System.out.println("\n=== SEARCH STUDENT ===");
        System.out.print("Search by (1. ID, 2. Name, 3. Type): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        String sql ;
        String param ;

        switch (choice) {
            case 1:
                System.out.print("Enter Student ID: ");
                param = scanner.nextLine();
                sql = "SELECT s.*, p.* FROM students s JOIN persons p ON s.person_id = p.person_id WHERE s.student_id = ?";
                break;
            case 2:
                System.out.print("Enter Name (partial): ");
                param = scanner.nextLine();
                sql = "SELECT s.*, p.* FROM students s JOIN persons p ON s.person_id = p.person_id WHERE p.name LIKE ?";
                param = "%" + param + "%";
                break;
            case 3:
                System.out.print("Enter Student Type: ");
                param = scanner.nextLine();
                sql = "SELECT s.*, p.* FROM students s JOIN persons p ON s.person_id = p.person_id WHERE p.person_subtype = ?";
                break;
            default:
                System.out.println("Invalid choice!");
                return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, param);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\nSearch Results:");
            System.out.println("ID\tName\t\tEmail\t\t\tGPA\tType");
            System.out.println("----------------------------------------------------------------");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-8s %-15s %-20s %-6.2f %s\n",
                        rs.getString("student_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getDouble("gpa"),
                        rs.getString("person_subtype"));
            }

            if (!found) {
                System.out.println("No students found!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void updateStudentGPA() {
        System.out.println("\n=== UPDATE STUDENT GPA ===");
        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter new GPA (0.0-4.0): ");
        double gpa = scanner.nextDouble();
        scanner.nextLine();

        String sql = SQLQueries.UPDATE_STUDENT_GPA;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, gpa);
            pstmt.setDouble(2, 0); // balance unchanged
            pstmt.setString(3, id);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("GPA updated successfully!");
            } else {
                System.out.println("Student not found!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void deleteStudent() {
        System.out.println("\n=== DELETE STUDENT ===");
        System.out.print("Enter Student ID to delete: ");
        String id = scanner.nextLine();

        System.out.print("Are you sure? (YES/NO): ");
        String confirm = scanner.nextLine();

        if (!confirm.equalsIgnoreCase("YES")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        String sql = SQLQueries.DELETE_PERSON;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Student deleted successfully!");
            } else {
                System.out.println("Student not found!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void enrollInCourse() {
        System.out.println("\n=== ENROLL STUDENT IN COURSE ===");
        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();

        System.out.print("Enter Course Code: ");
        String courseCode = scanner.nextLine();

        String sql = SQLQueries.INSERT_ENROLLMENT;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            pstmt.setString(2, courseCode);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Enrollment successful!");
            } else {
                System.out.println("Enrollment failed!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewStudentSchedule() {
        System.out.println("\n=== VIEW STUDENT SCHEDULE ===");
        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();

        String sql = SQLQueries.SELECT_ENROLLMENTS_BY_STUDENT;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\nEnrolled Courses:");
            System.out.println("Course Code\tCourse Title");
            System.out.println("--------------------------------");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-12s\t%s\n",
                        rs.getString("course_code"),
                        rs.getString("title"));
            }

            if (!found) {
                System.out.println("No courses enrolled!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewStudentPayments() {
        System.out.println("\n=== VIEW STUDENT PAYMENTS ===");
        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();

        String sql = SQLQueries.SELECT_PAYMENTS_BY_PERSON;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            pstmt.setString(2, studentId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\nPayment History:");
            System.out.println("Date\t\tType\t\tAmount\tDescription");
            System.out.println("------------------------------------------------");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%s\t%-12s\t%.2f\t%s\n",
                        rs.getTimestamp("payment_date").toString().substring(0, 10),
                        rs.getString("payment_type"),
                        rs.getDouble("amount"),
                        rs.getString("description"));
            }

            if (!found) {
                System.out.println("No payments found!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}