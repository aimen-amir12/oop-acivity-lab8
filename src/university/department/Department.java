package university.department;

import university.people.Faculty;
import university.course.Course;

public class Department {

    private String name;

    private Faculty[] facultyList = new Faculty[20];
    private int facultyCount = 0;

    private Course[] courses = new Course[20];
    private int courseCount = 0;

    private double budget;  // composition-owned by department

    public Department(String name, double budget) {
        this.name = name;
        this.budget = budget;
    }

    public void addFaculty(Faculty f) {
        if (facultyCount < facultyList.length) {
            facultyList[facultyCount] = f;
            facultyCount++;
        }
    }

    public void addCourse(Course c) {
        if (courseCount < courses.length) {
            courses[courseCount] = c;
            courseCount++;
        }
    }

    public void showFaculty() {
        System.out.println("Faculty in Department " + name + ":");
        for (int i = 0; i < facultyCount; i++) {
            System.out.println("- " + facultyList[i].getName());
        }
    }

    public void showCourses() {
        System.out.println("Courses in Department " + name + ":");
        for (int i = 0; i < courseCount; i++) {
            System.out.println("- " + courses[i].getCourseCode());
        }
    }
}
