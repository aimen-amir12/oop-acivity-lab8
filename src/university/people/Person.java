package university.people;

import university.database.DatabaseConnection;
import university.database.SQLQueries;
import java.sql.*;

public abstract class Person {
    private String id;
    private String name;
    private String email;
    private String phone;

    public Person(String id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    // Abstract methods
    public abstract String getRole();
    public abstract String getPersonType();
    public abstract void register();
    public abstract double calculatePayment();
    public abstract void displayDashboard();

    // ============ DATABASE OPERATIONS ============

    public boolean saveToDatabase() {
        String sql = SQLQueries.INSERT_PERSON;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, this.id);
            pstmt.setString(2, this.name);
            pstmt.setString(3, this.email);
            pstmt.setString(4, this.phone);
            pstmt.setString(5, getPersonType());
            pstmt.setString(6, getRole());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error saving person: " + e.getMessage());
            return false;
        }
    }


    public static Person loadFromDatabase(String personId) {
        String sql = SQLQueries.SELECT_PERSON_BY_ID;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, personId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String type = rs.getString("person_type");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String subtype = rs.getString("person_subtype");

                // Create appropriate subclass based on person_type and subtype
                switch (type) {
                    case "Student":
                        // Load student-specific data
                        String studentSql = "SELECT * FROM students WHERE student_id = ?";
                        try (PreparedStatement studentStmt = conn.prepareStatement(studentSql)) {
                            studentStmt.setString(1, personId);
                            ResultSet studentRs = studentStmt.executeQuery();
                            if (studentRs.next()) {
                                double gpa = studentRs.getDouble("gpa");
                                double balance = studentRs.getDouble("total_balance");
                                String advisor = studentRs.getString("advisor");

                                // Create appropriate student subclass
                                switch (subtype) {
                                    case "Undergraduate":
                                        UndergraduateStudent ug = new UndergraduateStudent(personId, name, email, phone);
                                        ug.updateGPA(gpa);
                                        ug.addFee(balance);
                                        return ug;
                                    case "Graduate":
                                        GraduateStudent gs = new GraduateStudent(personId, name, email, phone, advisor);
                                        gs.updateGPA(gpa);
                                        gs.addFee(balance);
                                        return gs;
                                    case "PhD Student":
                                        PhDStudent phd = new PhDStudent(personId, name, email, phone);
                                        phd.updateGPA(gpa);
                                        phd.addFee(balance);
                                        return phd;
                                }
                            }
                        }
                        break;

                    case "Faculty":
                        // Load faculty-specific data
                        String facultySql = "SELECT * FROM faculty WHERE faculty_id = ?";
                        try (PreparedStatement facultyStmt = conn.prepareStatement(facultySql)) {
                            facultyStmt.setString(1, personId);
                            ResultSet facultyRs = facultyStmt.executeQuery();
                            if (facultyRs.next()) {
                                double salary = facultyRs.getDouble("salary");
                                double balance = facultyRs.getDouble("total_balance");

                                switch (subtype) {
                                    case "Professor":
                                        return new Professor(personId, name, email, phone, salary);
                                    case "Assistant Professor":
                                        return new AssistantProfessor(personId, name, email, phone, salary);
                                    case "Lecturer":
                                        return new Lecturer(personId, name, email, phone, salary);
                                }
                            }
                        }
                        break;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error loading person: " + e.getMessage());
        }
        return null;
    }

    public boolean deleteFromDatabase() {
        String sql = SQLQueries.DELETE_PERSON;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, this.id);
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting person: " + e.getMessage());
            return false;
        }
    }
}