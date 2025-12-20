package university.modules;

import university.course.Course;
import university.database.DatabaseConnection;
import university.database.SQLQueries;
import java.sql.*;
import java.util.Scanner;

public class CourseManager {
    private Scanner scanner = new Scanner(System.in);

    public void showMenu() {
        while (true) {
            System.out.println("\n=== COURSE MANAGEMENT ===");
            System.out.println("1. Add New Course");
            System.out.println("2. View All Courses");
            System.out.println("3. Search Course");
            System.out.println("4. Update Course");
            System.out.println("5. Delete Course");
            System.out.println("6. View Course Enrollment");
            System.out.println("7. Back to Main Menu");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: addCourse(); break;
                case 2: viewAllCourses(); break;
                case 3: searchCourse(); break;
                case 4: updateCourse(); break;
                case 5: deleteCourse(); break;
                case 6: viewCourseEnrollment(); break;
                case 7: return;
                default: System.out.println("Invalid choice!");
            }
        }
    }

    private void addCourse() {
        System.out.println("\n=== ADD NEW COURSE ===");

        System.out.print("Course Code: ");
        String code = scanner.nextLine();

        System.out.print("Course Title: ");
        String title = scanner.nextLine();

        System.out.print("Credits: ");
        int credits = scanner.nextInt();

        System.out.print("Capacity: ");
        int capacity = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Department: ");
        String department = scanner.nextLine();

        String sql = SQLQueries.INSERT_COURSE;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, code);
            pstmt.setString(2, title);
            pstmt.setInt(3, credits);
            pstmt.setInt(4, capacity);
            pstmt.setString(5, department);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Course added successfully!");
            } else {
                System.out.println("Failed to add course!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewAllCourses() {
        System.out.println("\n=== ALL COURSES ===");
        System.out.println("Code\tTitle\t\t\tCredits\tCapacity\tEnrollment\tDepartment");
        System.out.println("--------------------------------------------------------------------------------");

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQLQueries.SELECT_ALL_COURSES)) {

            while (rs.next()) {
                System.out.printf("%-6s\t%-20s\t%-7d\t%-8d\t%-10d\t%s\n",
                        rs.getString("course_code"),
                        rs.getString("title"),
                        rs.getInt("credits"),
                        rs.getInt("capacity"),
                        rs.getInt("current_enrollment"),
                        rs.getString("department"));
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void searchCourse() {
        System.out.println("\n=== SEARCH COURSE ===");
        System.out.print("Search by (1. Code, 2. Title, 3. Department): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        String sql = "";
        String param = "";

        switch (choice) {
            case 1:
                System.out.print("Enter Course Code: ");
                param = scanner.nextLine();
                sql = "SELECT * FROM courses WHERE course_code = ?";
                break;
            case 2:
                System.out.print("Enter Course Title (partial): ");
                param = scanner.nextLine();
                sql = "SELECT * FROM courses WHERE title LIKE ?";
                param = "%" + param + "%";
                break;
            case 3:
                System.out.print("Enter Department: ");
                param = scanner.nextLine();
                sql = "SELECT * FROM courses WHERE department LIKE ?";
                param = "%" + param + "%";
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
            System.out.println("Code\tTitle\t\tCredits\tCapacity");
            System.out.println("----------------------------------------");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-6s\t%-15s\t%-7d\t%d\n",
                        rs.getString("course_code"),
                        rs.getString("title"),
                        rs.getInt("credits"),
                        rs.getInt("capacity"));
            }

            if (!found) {
                System.out.println("No courses found!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void updateCourse() {
        System.out.println("\n=== UPDATE COURSE ===");
        System.out.print("Enter Course Code: ");
        String code = scanner.nextLine();

        System.out.print("Enter new Capacity: ");
        int capacity = scanner.nextInt();
        scanner.nextLine();

        String sql = SQLQueries.UPDATE_COURSE_CAPACITY;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, capacity);
            pstmt.setInt(2, 0); // enrollment unchanged
            pstmt.setString(3, code);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Course updated successfully!");
            } else {
                System.out.println("Course not found!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deleteCourse() {
        System.out.println("\n=== DELETE COURSE ===");
        System.out.print("Enter Course Code to delete: ");
        String code = scanner.nextLine();

        System.out.print("Are you sure? (YES/NO): ");
        String confirm = scanner.nextLine();

        if (!confirm.equalsIgnoreCase("YES")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        String sql = "DELETE FROM courses WHERE course_code = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, code);
            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Course deleted successfully!");
            } else {
                System.out.println("Course not found!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewCourseEnrollment() {
        System.out.println("\n=== VIEW COURSE ENROLLMENT ===");
        System.out.print("Enter Course Code: ");
        String courseCode = scanner.nextLine();

        String sql = """
            SELECT s.name, e.enrollment_date, e.grade 
            FROM enrollments e 
            JOIN students s ON e.student_id = s.student_id 
            WHERE e.course_code = ? AND e.status = 'ENROLLED'
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, courseCode);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\nEnrolled Students:");
            System.out.println("Name\t\tEnrollment Date\tGrade");
            System.out.println("----------------------------------------");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-15s\t%s\t\t%s\n",
                        rs.getString("name"),
                        rs.getDate("enrollment_date"),
                        rs.getDouble("grade") == 0 ? "N/A" : rs.getDouble("grade"));
            }

            if (!found) {
                System.out.println("No students enrolled!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}