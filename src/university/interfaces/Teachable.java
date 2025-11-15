package university.interfaces;

import university.course.Course;

public interface Teachable {
    void teach(Course course);
    void assignGrades(Course course);
    void holdOfficeHours();
}
