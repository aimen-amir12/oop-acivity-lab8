package university.interfaces;

import university.course.Course;

public interface Enrollable {
    void enrollInCourse(Course course);
    void dropCourse(Course course);
    void viewSchedule();
}
