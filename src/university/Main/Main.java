package university.Main;

import university.people.*;
import university.course.*;
import university.department.*;
import university.library.*;

public class Main {
    public static void main(String[] args) {

        // Students
        UndergraduateStudent u1 = new UndergraduateStudent("S1", "Ali", "ali@mail.com", "123");
        PhDStudent p1 = new PhDStudent("S2", "Sara", "sara@mail.com", "456");

        // Faculty
        Professor prof = new Professor("F1", "Dr. Ahmad", "ahmad@mail.com", "999", 200000);

        // Course
        CS101 cs = new CS101();
        prof.teach(cs);
        u1.enrollInCourse(cs);

        // Department
        Department csDept = new Department("Computer Science", 5000000);
        csDept.addFaculty(prof);
        csDept.addCourse(cs);

        csDept.showFaculty();
        csDept.showCourses();

        // Library
        Library lib = new Library();
        lib.addBook(new Book("Java Programming", "John Doe"));
        lib.showBooks();

        // Payment example
        u1.addFee(20000);
        System.out.println(u1.generateInvoice());
        u1.processPayment(10000);
        System.out.println(u1.generateInvoice());
    }
}
