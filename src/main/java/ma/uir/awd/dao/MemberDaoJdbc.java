package ma.uir.awd.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import ma.uir.awd.model.Admin;
import ma.uir.awd.model.Parent;
import ma.uir.awd.model.SchoolMember;
import ma.uir.awd.model.Student;
import ma.uir.awd.model.Teacher;

public class MemberDaoJdbc implements IMemberDao {

    private static final MemberDaoJdbc INSTANCE = new MemberDaoJdbc();

    // The queries that load the list of each profile, given the member's id.
    private static final String COURSES =
            "SELECT course FROM student_courses WHERE student_id = ? ORDER BY course";
    private static final String SUBJECTS =
            "SELECT subject FROM teacher_subjects WHERE teacher_id = ? ORDER BY subject";
    private static final String CHILDREN =
            "SELECT m.email FROM parent_child pc "
                    + "JOIN members m ON m.id = pc.student_id "
                    + "WHERE pc.parent_id = ? ORDER BY m.email";

    private MemberDaoJdbc() { }

    public static MemberDaoJdbc getInstance() {
        return INSTANCE;
    }

    @Override
    public SchoolMember findByEmail(String email) {
        if (email == null) {
            return null;
        }
        String sql = "SELECT id, email, password, full_name, role FROM members WHERE email = ?";
        try {
            Connection c = SingletonConnection.getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return toMember(rs);
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SchoolMember> findAll() {
        String sql = "SELECT id, email, password, full_name, role FROM members ORDER BY full_name";
        List<SchoolMember> result = new ArrayList<>();
        try {
            Connection c = SingletonConnection.getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(toMember(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public void save(SchoolMember member) {
        // TODO 1: insert the row, or update it if the email already exists, in ONE statement
        String upsert =
                "INSERT INTO members (email, password, full_name, role) "
                        + "VALUES (?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE password = VALUES(password), "
                        + "full_name = VALUES(full_name), role = VALUES(role)";

        try {
            Connection c = SingletonConnection.getConnection();

            String role = member.getClass().getSimpleName().toLowerCase();

            try (PreparedStatement ps = c.prepareStatement(upsert)) {
                ps.setString(1, member.getEmail());
                ps.setString(2, member.getPassword());
                ps.setString(3, member.getFullName());
                ps.setString(4, role);
                ps.executeUpdate();
            }

            // TODO 2: get the member's id
            int id = idOf(member.getEmail());

            // TODO 3: write the profile's list with replaceList(...)
            if (member instanceof Student) {
                Student s = (Student) member;
                replaceList(id,
                        "DELETE FROM student_courses WHERE student_id = ?",
                        "INSERT INTO student_courses (student_id, course) VALUES (?, ?)",
                        s.getCourses());
            } else if (member instanceof Teacher) {
                Teacher t = (Teacher) member;
                replaceList(id,
                        "DELETE FROM teacher_subjects WHERE teacher_id = ?",
                        "INSERT INTO teacher_subjects (teacher_id, subject) VALUES (?, ?)",
                        t.getSubjects());
            } else if (member instanceof Parent) {
                Parent p = (Parent) member;
                replaceList(id,
                        "DELETE FROM parent_child WHERE parent_id = ?",
                        "INSERT INTO parent_child (parent_id, student_id) "
                                + "SELECT ?, id FROM members WHERE email = ? AND role = 'student'",
                        p.getChildren());
            }
            // Admin: nothing

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean delete(String email) {
        String sql = "DELETE FROM members WHERE email = ?";
        try {
            Connection c = SingletonConnection.getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, email);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ------------------------------------------------------------- helpers

    /** Turns the current row of rs into the right Java object. */
    private SchoolMember toMember(ResultSet rs) throws SQLException {
        int id          = rs.getInt("id");
        String email    = rs.getString("email");
        String password = rs.getString("password");
        String fullName = rs.getString("full_name");
        String role     = rs.getString("role");

        SchoolMember m;
        switch (role) {
            case "student":
                Student s = new Student(fullName, email, password);
                for (String course : loadList(COURSES, id)) {
                    s.enroll(course);
                }
                m = s;
                break;
            case "teacher":
                Teacher t = new Teacher(fullName, email, password);
                for (String subject : loadList(SUBJECTS, id)) {
                    t.assignSubject(subject);
                }
                m = t;
                break;
            case "parent":
                Parent p = new Parent(fullName, email, password);
                for (String child : loadList(CHILDREN, id)) {
                    p.followChild(child);
                }
                m = p;
                break;
            case "admin":
                m = new Admin(fullName, email, password);
                break;
            default:
                throw new IllegalStateException("Unknown role: " + role);
        }
        return m;
    }

    /** The id of the member with this email (the member must exist). */
    private int idOf(String email) throws SQLException {
        Connection c = SingletonConnection.getConnection();
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT id FROM members WHERE email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    /** Runs a query with one int parameter; returns the first column. */
    private List<String> loadList(String sql, int ownerId) throws SQLException {
        List<String> values = new ArrayList<>();
        Connection c = SingletonConnection.getConnection();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) values.add(rs.getString(1));
            }
        }
        return values;
    }

    /** Deletes the owner's rows, then inserts one row per value. */
    private void replaceList(int ownerId, String deleteSql, String insertSql,
                             List<String> values) throws SQLException {
        Connection c = SingletonConnection.getConnection();
        try (PreparedStatement del = c.prepareStatement(deleteSql)) {
            del.setInt(1, ownerId);
            del.executeUpdate();
        }
        try (PreparedStatement ins = c.prepareStatement(insertSql)) {
            for (String value : values) {
                ins.setInt(1, ownerId);
                ins.setString(2, value);
                ins.executeUpdate();
            }
        }
    }
}