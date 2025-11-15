package university.people;

import university.interfaces.Payable;
import university.interfaces.Teachable;
import university.course.Course;

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

    // Faculty teaches courses
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

    // Payable methods
    @Override
    public void processPayment(double amount) {
        balance += amount;
    }

    @Override
    public String generateInvoice() {
        return "Salary summary for " + getName() + ": Received = " + balance;
    }

    @Override
    public double getFinancialSummary() {
        return balance;
    }
}
