package university.course;

import university.people.Student;

public class CS101 extends Course {

    public CS101() {
        super("CS101", "Introduction to Programming", 50);
    }

    @Override
    public double calculateFinalGrade(Student s) {
        return 85.0; // default dummy value
    }

    @Override
    public boolean checkPrerequisites(Student s) {
        return true; // no prerequisites
    }

    @Override
    public void generateSyllabus() {
        System.out.println("Syllabus: Basics of Java, Variables, Loops, OOP");
    }
}
