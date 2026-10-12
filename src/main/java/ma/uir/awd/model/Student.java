package ma.uir.awd.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Student extends SchoolMember {

    private final List<String> courses = new ArrayList<>();

    public Student(String fullName, String email, String password) {
        super(fullName, email, password);
    }

    public void enroll(String course) {
        if (course == null || course.isBlank() || courses.contains(course)) {
            return;
        }
        courses.add(course);
    }

    public List<String> getCourses() {
        return Collections.unmodifiableList(courses);
    }
}
