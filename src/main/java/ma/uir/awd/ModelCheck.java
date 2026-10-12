package ma.uir.awd;

import ma.uir.awd.model.Student;
import ma.uir.awd.model.Teacher;

// Plain Java: runs without Tomcat, like any class with a main().
public class ModelCheck {
    public static void main(String[] args) {
        Student s = new Student("Ayoub", "ayoub@uir.ac.ma", "secret");
        s.enroll("Web Development");
        s.enroll("Web Development"); // duplicate: must be ignored
        s.enroll(" ");                // blank: must be ignored
        System.out.println(s.getFullName() + " -> " + s.getCourses());
        System.out.println("secret ok? " + s.checkPassword("secret"));
        System.out.println("SECRET ok? " + s.checkPassword("SECRET"));

        Teacher t = new Teacher("Saad", "saad@uir.ac.ma", "pwd");
        t.assignSubject("JEE");
        System.out.println(t.getFullName() + " -> " + t.getSubjects());
        s.getCourses().add("Machine Learning");
    }

}
