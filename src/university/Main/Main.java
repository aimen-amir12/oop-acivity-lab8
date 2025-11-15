package university.main;

import university.people.*;
import university.course.*;
import university.department.*;
import university.library.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("===== UNIVERSITY MANAGEMENT SYSTEM =====\n");

        // ===========================================================
        // 1. CREATE STUDENTS
        // ===========================================================
        UndergraduateStudent ug = new UndergraduateStudent(
                "UG01", "Ali", "ali@uni.edu", "0300-1234567");

        GraduateStudent grad = new GraduateStudent(
                "GR01", "Hassan", "hassan@uni.edu", "0301-2222222", "Dr. Salman");

        PhDStudent phd = new PhDStudent(
                "PHD01", "Sara", "sara@uni.edu", "0312-9999999");

        ug.register();
        grad.register();
        phd.register();

        System.out.println();


        // ===========================================================
        // 2. CREATE FACULTY
        // ===========================================================
        Professor prof = new Professor(
                "F01", "Dr. Ahmad", "ahmad@uni.edu", "0333-8888888", 250000);

        AssistantProfessor asst = new AssistantProfessor(
                "F02", "Dr. Farah", "farah@uni.edu", "0334-7777777", 180000);

        Lecturer lect = new Lecturer(
                "F03", "Mr. Bilal", "bilal@uni.edu", "0332-6666666", 120000);

        prof.register();
        asst.register();
        lect.register();

        System.out.println();


        // ===========================================================
        // 3. CREATE STAFF
        // ===========================================================
        Administrator admin = new Administrator(
                "ST01", "Nadia", "nadia@uni.edu", "0345-1122334", 90000);

        TechnicalStaff tech = new TechnicalStaff(
                "ST02", "Kamran", "kamran@uni.edu", "0342-4455669", 70000);

        Librarian librarian = new Librarian(
                "ST03", "Ayesha", "ayesha@uni.edu", "0341-5566778", 65000);

        admin.register();
        tech.register();
        librarian.register();

        System.out.println();


        // ===========================================================
        // 4. CREATE COURSE
        // ===========================================================
        CS101 cs101 = new CS101();

        // Professor teaches CS101
        prof.teach(cs101);

        // Students enroll
        ug.enrollInCourse(cs101);
        grad.enrollInCourse(cs101);
        phd.enrollInCourse(cs101);

        System.out.println();


        // ===========================================================
        // 5. CREATE DEPARTMENT
        // ===========================================================
        Department csDept = new Department("Computer Science", 5000000);

        csDept.addFaculty(prof);
        csDept.addFaculty(asst);
        csDept.addFaculty(lect);

        csDept.addCourse(cs101);

        System.out.println("===== Department Details =====");
        csDept.showFaculty();
        csDept.showCourses();

        System.out.println();


        // ===========================================================
        // 6. LIBRARY
        // ===========================================================
        Library lib = new Library();
        lib.addBook(new Book("Java Programming", "John Doe"));
        lib.addBook(new Book("Object Oriented Design", "Grady Booch"));
        lib.addBook(new Book("Data Structures & Algorithms", "Mark Allen Weiss"));

        System.out.println("===== Library Books =====");
        lib.showBooks();

        System.out.println();


        // ===========================================================
        // 7. PAYMENTS (Payable Interface)
        // ===========================================================
        System.out.println("===== Payment System =====");

        // Adding fee for student
        ug.addFee(20000);
        System.out.println(ug.generateInvoice());
        ug.processPayment(10000);
        System.out.println(ug.generateInvoice());

        // Paying salary to professor
        prof.processPayment(250000);
        System.out.println(prof.generateInvoice());

        // Paying administrator
        admin.processPayment(90000);
        System.out.println(admin.generateInvoice());

        System.out.println();


        // ===========================================================
        // 8. DISPLAY DASHBOARDS (POLYMORPHISM)
        // ===========================================================
        System.out.println("===== Dashboards =====");

        Person[] people = { ug, grad, phd, prof, asst, lect, admin, tech, librarian };

        for (int i = 0; i < people.length; i++) {
            Person p = people[i];

            System.out.println("\nRole: " + p.getRole());
            p.displayDashboard();
        }


        System.out.println("\n===== SYSTEM TEST COMPLETE =====");
    }
}
