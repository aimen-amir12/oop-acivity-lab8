package university.modules;

import university.database.DatabaseConnection;
import university.database.SQLQueries;
import java.sql.*;
import java.util.Scanner;

public class GradingManager {
    private static Scanner scanner = new Scanner(System.in);

    public static void showMenu() {
        while (true) {
            System.out.println("\n=== GRADING MANAGEMENT ===");
            System.out.println("1. Assign Grades to Student");
            System.out.println("2. Update Student Grade");
            System.out.println("3. View Student Grades");
            System.out.println("4. Calculate Student GPA");
            System.out.println("5. Generate Student Transcript");
            System.out.println("6. View Course Grades Report");
            System.out.println("7. Back to Main Menu");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: assignGrades(); break;
                case 2: updateGrade(); break;
                case 3: viewStudentGrades(); break;
                case 4: calculateGPA(); break;
                case 5: generateTranscript(); break;
                case 6: viewCourseGradesReport(); break;
                case 7: return;
                default: System.out.println("Invalid choice!");
            }
        }
    }

    private static void assignGrades() {
        System.out.println("\n=== ASSIGN GRADES ===");
        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();

        System.out.print("Enter Course Code: ");
        String courseCode = scanner.nextLine();

        System.out.print("Enter Grade (0-100): ");
        double grade = scanner.nextDouble();
        scanner.nextLine();

        if (grade < 0 || grade > 100) {
            System.out.println("Grade must be between 0 and 100!");
            return;
        }

        String sql = SQLQueries.UPDATE_ENROLLMENT_GRADE;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, grade);
            pstmt.setString(2, studentId);
            pstmt.setString(3, courseCode);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Grade assigned successfully!");
                updateStudentGPA(studentId);
            } else {
                System.out.println("Student not enrolled in this course!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void updateGrade() {
        System.out.println("\n=== UPDATE GRADE ===");
        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();

        System.out.print("Enter Course Code: ");
        String courseCode = scanner.nextLine();

        System.out.print("Enter New Grade (0-100): ");
        double newGrade = scanner.nextDouble();
        scanner.nextLine();

        String sql = SQLQueries.UPDATE_ENROLLMENT_GRADE;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, newGrade);
            pstmt.setString(2, studentId);
            pstmt.setString(3, courseCode);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Grade updated successfully!");
                updateStudentGPA(studentId);
            } else {
                System.out.println("No such enrollment found!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void updateStudentGPA(String studentId) {
        String sql = """
            SELECT AVG(grade) as avg_grade 
            FROM enrollments 
            WHERE student_id = ? AND grade IS NOT NULL
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double avgGrade = rs.getDouble("avg_grade");
                if (!rs.wasNull()) {
                    // Convert 0-100 scale to 0-4.0 GPA scale
                    double gpa = (avgGrade / 100.0) * 4.0;

                    String updateSql = "UPDATE students SET gpa = ? WHERE student_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setDouble(1, gpa);
                        updateStmt.setString(2, studentId);
                        updateStmt.executeUpdate();
                        System.out.println("Student GPA updated to: " + String.format("%.2f", gpa));
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error updating GPA: " + e.getMessage());
        }
    }

    private static void viewStudentGrades() {
        System.out.println("\n=== VIEW STUDENT GRADES ===");
        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();

        String sql = """
            SELECT c.course_code, c.title, c.credits, e.grade, 
                   CASE 
                       WHEN e.grade >= 85 THEN 'A'
                       WHEN e.grade >= 80 THEN 'A-'
                       WHEN e.grade >= 75 THEN 'B+'
                       WHEN e.grade >= 70 THEN 'B'
                       WHEN e.grade >= 65 THEN 'B-'
                       WHEN e.grade >= 60 THEN 'C+'
                       WHEN e.grade >= 55 THEN 'C'
                       WHEN e.grade >= 50 THEN 'C-'
                       ELSE 'F'
                   END as letter_grade
            FROM enrollments e
            JOIN courses c ON e.course_code = c.course_code
            WHERE e.student_id = ? AND e.grade IS NOT NULL
            ORDER BY e.enrollment_date DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\nGrades for Student: " + studentId);
            System.out.println("Course Code\tTitle\t\t\tCredits\tGrade\tLetter Grade");
            System.out.println("------------------------------------------------------------");

            boolean found = false;
            double totalPoints = 0;
            int totalCredits = 0;

            while (rs.next()) {
                found = true;
                double grade = rs.getDouble("grade");
                int credits = rs.getInt("credits");
                String letterGrade = rs.getString("letter_grade");

                System.out.printf("%-12s\t%-20s\t%-7d\t%-5.1f\t%s\n",
                        rs.getString("course_code"),
                        rs.getString("title"),
                        credits,
                        grade,
                        letterGrade);

                totalPoints += (grade / 100.0 * 4.0) * credits;
                totalCredits += credits;
            }

            if (found) {
                double gpa = totalCredits > 0 ? totalPoints / totalCredits : 0;
                System.out.println("------------------------------------------------------------");
                System.out.printf("Current GPA: %.2f (Based on %d credits)\n", gpa, totalCredits);
            } else {
                System.out.println("No grades found for this student!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void calculateGPA() {
        System.out.println("\n=== CALCULATE GPA ===");
        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();

        updateStudentGPA(studentId);
    }

    private static void generateTranscript() {
        System.out.println("\n=== GENERATE TRANSCRIPT ===");
        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine();

        String studentSql = "SELECT s.*, p.name FROM students s JOIN persons p ON s.person_id = p.person_id WHERE s.student_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(studentSql)) {

            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("\n" + "=".repeat(60));
                System.out.println("              ACADEMIC TRANSCRIPT");
                System.out.println("=".repeat(60));
                System.out.println("Student Name: " + rs.getString("name"));
                System.out.println("Student ID:   " + rs.getString("student_id"));
                System.out.println("Current GPA:  " + String.format("%.2f", rs.getDouble("gpa")));
                System.out.println("Total Balance: Rs. " + String.format("%.2f", rs.getDouble("total_balance")));
                System.out.println("=".repeat(60));

                // Get all courses with grades
                String gradesSql = """
                    SELECT c.course_code, c.title, c.credits, e.grade, e.enrollment_date,
                           CASE 
                               WHEN e.grade >= 85 THEN 'A (4.0)'
                               WHEN e.grade >= 80 THEN 'A- (3.7)'
                               WHEN e.grade >= 75 THEN 'B+ (3.3)'
                               WHEN e.grade >= 70 THEN 'B (3.0)'
                               WHEN e.grade >= 65 THEN 'B- (2.7)'
                               WHEN e.grade >= 60 THEN 'C+ (2.3)'
                               WHEN e.grade >= 55 THEN 'C (2.0)'
                               WHEN e.grade >= 50 THEN 'C- (1.7)'
                               ELSE 'F (0.0)'
                           END as grade_info
                    FROM enrollments e
                    JOIN courses c ON e.course_code = c.course_code
                    WHERE e.student_id = ? AND e.grade IS NOT NULL
                    ORDER BY e.enrollment_date
                    """;

                try (PreparedStatement gradesStmt = conn.prepareStatement(gradesSql)) {
                    gradesStmt.setString(1, studentId);
                    ResultSet gradesRs = gradesStmt.executeQuery();

                    System.out.println("\nCOURSE HISTORY:");
                    System.out.println("Code\tTitle\t\t\tCredits\tGrade\tDate");
                    System.out.println("-".repeat(60));

                    double totalQualityPoints = 0;
                    int totalCredits = 0;

                    while (gradesRs.next()) {
                        String courseCode = gradesRs.getString("course_code");
                        String title = gradesRs.getString("title");
                        int credits = gradesRs.getInt("credits");
                        double grade = gradesRs.getDouble("grade");
                        String gradeInfo = gradesRs.getString("grade_info");
                        Date enrollDate = gradesRs.getDate("enrollment_date");

                        // Calculate quality points
                        double qualityPoints = (grade / 100.0 * 4.0) * credits;
                        totalQualityPoints += qualityPoints;
                        totalCredits += credits;

                        System.out.printf("%-6s\t%-20s\t%-7d\t%-5.1f\t%s\n",
                                courseCode, title, credits, grade, enrollDate);
                    }

                    if (totalCredits > 0) {
                        double calculatedGPA = totalQualityPoints / totalCredits;
                        System.out.println("-".repeat(60));
                        System.out.printf("Cumulative GPA: %.2f (Based on %d credits)\n", calculatedGPA, totalCredits);
                    }
                }

            } else {
                System.out.println("Student not found!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewCourseGradesReport() {
        System.out.println("\n=== COURSE GRADES REPORT ===");
        System.out.print("Enter Course Code: ");
        String courseCode = scanner.nextLine();

        String sql = """
            SELECT p.name, e.grade,
                   CASE 
                       WHEN e.grade >= 85 THEN 'A'
                       WHEN e.grade >= 80 THEN 'A-'
                       WHEN e.grade >= 75 THEN 'B+'
                       WHEN e.grade >= 70 THEN 'B'
                       WHEN e.grade >= 65 THEN 'B-'
                       WHEN e.grade >= 60 THEN 'C+'
                       WHEN e.grade >= 55 THEN 'C'
                       WHEN e.grade >= 50 THEN 'C-'
                       ELSE 'F'
                   END as letter_grade
            FROM enrollments e
            JOIN students s ON e.student_id = s.student_id
            JOIN persons p ON s.person_id = p.person_id
            WHERE e.course_code = ? AND e.grade IS NOT NULL
            ORDER BY e.grade DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, courseCode);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\nGrade Distribution for Course: " + courseCode);
            System.out.println("Student Name\t\tGrade\tLetter Grade");
            System.out.println("----------------------------------------");

            boolean found = false;
            int aCount = 0, bCount = 0, cCount = 0, fCount = 0;
            double total = 0;
            int count = 0;

            while (rs.next()) {
                found = true;
                String name = rs.getString("name");
                double grade = rs.getDouble("grade");
                String letterGrade = rs.getString("letter_grade");

                System.out.printf("%-20s\t%.1f\t%s\n", name, grade, letterGrade);

                // Count grade distribution
                if (grade >= 85) aCount++;
                else if (grade >= 70) bCount++;
                else if (grade >= 50) cCount++;
                else fCount++;

                total += grade;
                count++;
            }

            if (found) {
                System.out.println("\n=== GRADE DISTRIBUTION ===");
                System.out.println("A/A-: " + aCount + " students");
                System.out.println("B+/B/B-: " + bCount + " students");
                System.out.println("C+/C/C-: " + cCount + " students");
                System.out.println("F: " + fCount + " students");
                System.out.println("Class Average: " + String.format("%.1f", total/count));
            } else {
                System.out.println("No grades recorded for this course!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}