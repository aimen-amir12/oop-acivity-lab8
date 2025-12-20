package university.people;

import university.interfaces.Payable;
import university.interfaces.Teachable;
import university.course.Course;
import university.database.DatabaseConnection;
import university.database.SQLQueries;
import java.sql.*;

public abstract class Faculty extends Person implements Teachable, Payable {
    private double salary;
    private Course[] assignedCourses = new Course[5];
    private int courseCount = 0;
    private double balance = 0;

    public Faculty(String id, String name, String email, String phone, double salary) {
        super(id, name, email, phone);
        this.salary = salary;
    }

    public double getSalary() {
        return salary;
    }

    protected void updateSalary(double amount) {
        this.salary = amount;
    }

    @Override
    public String getPersonType() {
        return "Faculty";
    }

    // Teaching methods
    @Override
    public void teach(Course course) {
        if (courseCount < assignedCourses.length) {
            assignedCourses[courseCount] = course;
            courseCount++;
            System.out.println(getName() + " is now teaching: " + course.getCourseCode());
        } else {
            System.out.println("Teaching load full for " + getName());
        }
    }

    @Override
    public void assignGrades(Course course) {
        System.out.println(getName() + " assigning grades for " + course.getCourseCode());
    }

    @Override
    public void holdOfficeHours() {
        System.out.println(getName() + " is holding office hours.");
    }

    // Payment methods
    @Override
    public void processPayment(double amount) {
        balance += amount;
        recordPaymentToDatabase(amount, "SALARY_PAYMENT");
    }

    @Override
    public String generateInvoice() {
        return "Salary summary for " + getName() + ": Received = " + balance;
    }

    @Override
    public double getFinancialSummary() {
        return balance;
    }

    // ============ DATABASE METHODS ============

    @Override
    public boolean saveToDatabase() {
        // Save to persons table first
        boolean personSaved = super.saveToDatabase();

        if (!personSaved) return false;

        // Save to faculty table
        String sql = """
            INSERT INTO faculty (faculty_id, person_id, salary, total_balance) 
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, getId());
            pstmt.setString(2, getId());
            pstmt.setDouble(3, this.salary);
            pstmt.setDouble(4, this.balance);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error saving faculty: " + e.getMessage());
            return false;
        }
    }

    private void recordPaymentToDatabase(double amount, String type) {
        String sql = SQLQueries.INSERT_PAYMENT;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "UNIVERSITY"); // payer
            pstmt.setString(2, getId()); // payee
            pstmt.setDouble(3, amount);
            pstmt.setString(4, type);
            pstmt.setString(5, "Salary payment for " + getName());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error recording payment: " + e.getMessage());
        }
    }
}