package university.people;

import university.interfaces.Enrollable;
import university.interfaces.Payable;
import university.course.Course;
import university.database.DatabaseConnection;
import university.database.SQLQueries;
import java.sql.*;

public abstract class Student extends Person implements Enrollable, Payable {
    private Course[] enrolledCourses = new Course[10];
    private int courseCount = 0;
    private double gpa;
    private double balance = 0;

    public Student(String id, String name, String email, String phone) {
        super(id, name, email, phone);
    }

    // Getters
    public Course[] getEnrolledCourses() { return enrolledCourses; }
    public int getCourseCount() { return courseCount; }
    public double getGpa() { return gpa; }
    public void updateGPA(double newGPA) { this.gpa = newGPA; }

    @Override
    public String getPersonType() {
        return "Student";
    }

    // Enrollment methods
    @Override
    public void enrollInCourse(Course course) {
        if (courseCount < enrolledCourses.length) {
            enrolledCourses[courseCount] = course;
            courseCount++;
            System.out.println(getName() + " enrolled in " + course.getCourseCode());
            saveEnrollmentToDatabase(course);
        } else {
            System.out.println("Course limit reached for " + getName());
        }
    }

    @Override
    public void dropCourse(Course course) {
        for (int i = 0; i < courseCount; i++) {
            if (enrolledCourses[i] == course) {
                for (int j = i; j < courseCount - 1; j++) {
                    enrolledCourses[j] = enrolledCourses[j + 1];
                }
                courseCount--;
                System.out.println(getName() + " dropped " + course.getCourseCode());
                deleteEnrollmentFromDatabase(course);
                return;
            }
        }
        System.out.println("Course not found.");
    }

    @Override
    public void viewSchedule() {
        System.out.println("Schedule for " + getName() + ":");
        for (int i = 0; i < courseCount; i++) {
            System.out.println("- " + enrolledCourses[i].getCourseCode());
        }
    }

    // Payment methods
    @Override
    public void processPayment(double amount) {
        balance -= amount;
        System.out.println(getName() + " paid: " + amount);
        recordPaymentToDatabase(amount, "TUITION_PAYMENT");
    }

    @Override
    public String generateInvoice() {
        return "Invoice for " + getName() + ": Outstanding balance = " + balance;
    }

    @Override
    public double getFinancialSummary() {
        return balance;
    }

    public void addFee(double amount) {
        balance += amount;
    }

    // ============ DATABASE METHODS ============

    @Override
    public boolean saveToDatabase() {
        // Save to persons table first
        boolean personSaved = super.saveToDatabase();

        if (!personSaved) return false;

        // Save to students table
        String sql = SQLQueries.INSERT_STUDENT;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, getId()); // student_id
            pstmt.setString(2, getId()); // person_id (same as student_id)
            pstmt.setString(3, getAdvisor()); // For graduate students
            pstmt.setDouble(4, this.gpa);
            pstmt.setDouble(5, this.balance);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error saving student: " + e.getMessage());
            return false;
        }
    }


    protected String getAdvisor() {
        return null;
    }

    private void saveEnrollmentToDatabase(Course course) {
        String sql = SQLQueries.INSERT_ENROLLMENT;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, getId());
            pstmt.setString(2, course.getCourseCode());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error saving enrollment: " + e.getMessage());
        }
    }

    private void deleteEnrollmentFromDatabase(Course course) {
        String sql = SQLQueries.DELETE_ENROLLMENT;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, getId());
            pstmt.setString(2, course.getCourseCode());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error deleting enrollment: " + e.getMessage());
        }
    }

    private void recordPaymentToDatabase(double amount, String type) {
        String sql = SQLQueries.INSERT_PAYMENT;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, getId()); // payer
            pstmt.setString(2, "UNIVERSITY"); // payee
            pstmt.setDouble(3, amount);
            pstmt.setString(4, type);
            pstmt.setString(5, "Tuition payment for " + getName());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error recording payment: " + e.getMessage());
        }
    }

    public static ResultSet getAllStudentsFromDatabase() throws SQLException {
        String sql = SQLQueries.SELECT_ALL_STUDENTS;
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }
}