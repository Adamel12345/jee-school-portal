package ma.uir.awd.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Parent extends SchoolMember {

    // The emails of the students this parent follows.
    private final List<String> children = new ArrayList<>();

    public Parent(String fullName, String email, String password) {
        super(fullName, email, password);
    }

    public void followChild(String studentEmail) {
        if (studentEmail == null || studentEmail.isBlank()
                || children.contains(studentEmail)) {
            return;
        }
        children.add(studentEmail);
    }

    public List<String> getChildren() {
        return Collections.unmodifiableList(children);
    }
}