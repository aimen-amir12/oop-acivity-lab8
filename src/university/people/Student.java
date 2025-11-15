package university.people;

import university.interfaces.Enrollable;
import university.interfaces.Payable;
import university.course.Course;

public abstract class Student extends Person implements Enrollable, Payable {

    private Course[] enrolledCourses = new Course[10];   // max 10 courses
    private int courseCount = 0;

    private double gpa;
    private double balance = 0; // money owed (tuition fees)

    public Student(String id, String name, String email, String phone) {
        super(id, name, email, phone);
    }

    @Override
    public void enrollInCourse(Course course) {
        if (courseCount < enrolledCourses.length) {
            enrolledCourses[courseCount] = course;
            courseCount++;
            System.out.println(getName() + " enrolled in " + course.getCourseCode());
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

    public Course[] getEnrolledCourses() { return enrolledCourses; }
    public int getCourseCount() { return courseCount; }
    public double getGpa() { return gpa; }
    public void updateGPA(double newGPA) { this.gpa = newGPA; }


    @Override
    public void processPayment(double amount) {
        balance -= amount;
        System.out.println(getName() + " paid: " + amount);
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
        balance += amount;  // tuition fee
    }
}
