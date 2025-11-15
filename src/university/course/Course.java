package university.course;

import university.people.Student;

public abstract class Course {

    private String courseCode;
    private String title;

    private Student[] enrolledStudents = new Student[50]; // max 50 students
    private int studentCount = 0;

    private int capacity;

    public Course(String code, String title, int capacity) {
        this.courseCode = code;
        this.title = title;
        this.capacity = capacity;
    }

    public String getCourseCode() { return courseCode; }

    public void enrollStudent(Student s) {
        if (studentCount < capacity) {
            enrolledStudents[studentCount] = s;
            studentCount++;
        } else {
            System.out.println("Course full: " + courseCode);
        }
    }

    public abstract double calculateFinalGrade(Student s);
    public abstract boolean checkPrerequisites(Student s);
    public abstract void generateSyllabus();
}
