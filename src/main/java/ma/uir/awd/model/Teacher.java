package ma.uir.awd.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Teacher extends SchoolMember {

    private final List<String> subjects = new ArrayList<>();

    public Teacher(String fullName, String email, String password) {
        super(fullName, email, password);
    }

    public void assignSubject(String subject) {
        if (subject == null || subject.isBlank() || subjects.contains(subject)) {
            return;
        }
        subjects.add(subject);
    }

    public List<String> getSubjects() {
        return Collections.unmodifiableList(subjects);
    }
}