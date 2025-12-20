package university.modules;

import university.people.*;
import university.database.DatabaseConnection;
import university.database.SQLQueries;
import java.sql.*;
import java.util.Scanner;

public class FacultyManager {
    private Scanner scanner = new Scanner(System.in);

    public void showMenu() {
        while (true) {
            System.out.println("\n=== FACULTY MANAGEMENT ===");
            System.out.println("1. Add New Faculty");
            System.out.println("2. View All Faculty");
            System.out.println("3. Search Faculty");
            System.out.println("4. Update Faculty Salary");
            System.out.println("5. Delete Faculty");
            System.out.println("6. Assign Course to Faculty");
            System.out.println("7. View Faculty Payments");
            System.out.println("8. Back to Main Menu");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: addFaculty(); break;
                case 2: viewAllFaculty(); break;
                case 3: searchFaculty(); break;
                case 4: updateFacultySalary(); break;
                case 5: deleteFaculty(); break;
                case 6: assignCourseToFaculty(); break;
                case 7: viewFacultyPayments(); break;
                case 8: return;
                default: System.out.println("Invalid choice!");
            }
        }
    }

    private void addFaculty() {
        System.out.println("\n=== ADD NEW FACULTY ===");

        System.out.print("Faculty ID: ");
        String id = scanner.nextLine();

        System.out.print("Name: ");
        String name = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Phone: ");
        String phone = scanner.nextLine();

        System.out.print("Salary: ");
        double salary = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Faculty Type (1. Professor, 2. Assistant Professor, 3. Lecturer): ");
        int type = scanner.nextInt();
        scanner.nextLine();

        Faculty faculty = null;

        switch (type) {
            case 1:
                faculty = new Professor(id, name, email, phone, salary);
                break;
            case 2:
                faculty = new AssistantProfessor(id, name, email, phone, salary);
                break;
            case 3:
                faculty = new Lecturer(id, name, email, phone, salary);
                break;
            default:
                System.out.println("Invalid type!");
                return;
        }

        if (faculty.saveToDatabase()) {
            System.out.println("Faculty added successfully!");
            faculty.register();
        } else {
            System.out.println("Failed to add faculty!");
        }
    }

    private void viewAllFaculty() {
        System.out.println("\n=== ALL FACULTY ===");
        System.out.println("ID\tName\t\t\tEmail\t\t\tSalary\tBalance");
        System.out.println("--------------------------------------------------------------------");

        String sql = """
            SELECT f.*, p.name, p.email 
            FROM faculty f 
            JOIN persons p ON f.person_id = p.person_id 
            ORDER BY f.faculty_id
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.printf("%-8s %-15s %-20s %-10.2f %.2f\n",
                        rs.getString("faculty_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getDouble("salary"),
                        rs.getDouble("total_balance"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void searchFaculty() {
        System.out.println("\n=== SEARCH FACULTY ===");
        System.out.print("Search by (1. ID, 2. Name, 3. Type): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        String sql = "";
        String param = "";

        switch (choice) {
            case 1:
                System.out.print("Enter Faculty ID: ");
                param = scanner.nextLine();
                sql = """
                    SELECT f.*, p.* 
                    FROM faculty f 
                    JOIN persons p ON f.person_id = p.person_id 
                    WHERE f.faculty_id = ?
                    """;
                break;
            case 2:
                System.out.print("Enter Name (partial): ");
                param = scanner.nextLine();
                sql = """
                    SELECT f.*, p.* 
                    FROM faculty f 
                    JOIN persons p ON f.person_id = p.person_id 
                    WHERE p.name LIKE ?
                    """;
                param = "%" + param + "%";
                break;
            case 3:
                System.out.print("Enter Faculty Type: ");
                param = scanner.nextLine();
                sql = """
                    SELECT f.*, p.* 
                    FROM faculty f 
                    JOIN persons p ON f.person_id = p.person_id 
                    WHERE p.person_subtype = ?
                    """;
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
            System.out.println("ID\tName\t\tEmail\t\tSalary\tType");
            System.out.println("------------------------------------------------");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-8s %-15s %-20s %-10.2f %s\n",
                        rs.getString("faculty_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getDouble("salary"),
                        rs.getString("person_subtype"));
            }

            if (!found) {
                System.out.println("No faculty found!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void updateFacultySalary() {
        System.out.println("\n=== UPDATE FACULTY SALARY ===");
        System.out.print("Enter Faculty ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter new Salary: ");
        double salary = scanner.nextDouble();
        scanner.nextLine();

        String sql = "UPDATE faculty SET salary = ? WHERE faculty_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, salary);
            pstmt.setString(2, id);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Salary updated successfully!");
            } else {
                System.out.println("Faculty not found!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deleteFaculty() {
        System.out.println("\n=== DELETE FACULTY ===");
        System.out.print("Enter Faculty ID to delete: ");
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
                System.out.println("Faculty deleted successfully!");
            } else {
                System.out.println("Faculty not found!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void assignCourseToFaculty() {
        System.out.println("\n=== ASSIGN COURSE TO FACULTY ===");
        System.out.print("Enter Faculty ID: ");
        String facultyId = scanner.nextLine();

        System.out.print("Enter Course Code: ");
        String courseCode = scanner.nextLine();

        // This would typically update a faculty_courses table
        // For now, we'll just print a message
        System.out.println("Course " + courseCode + " assigned to faculty " + facultyId);
        System.out.println("(Implementation: Add to faculty_courses table)");
    }

    private void viewFacultyPayments() {
        System.out.println("\n=== VIEW FACULTY PAYMENTS ===");
        System.out.print("Enter Faculty ID: ");
        String facultyId = scanner.nextLine();

        String sql = SQLQueries.SELECT_PAYMENTS_BY_PERSON;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, facultyId);
            pstmt.setString(2, facultyId);
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