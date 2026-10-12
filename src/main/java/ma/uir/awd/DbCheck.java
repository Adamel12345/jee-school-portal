package ma.uir.awd;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import ma.uir.awd.dao.MemberDaoJdbc;
import ma.uir.awd.dao.SingletonConnection;
import ma.uir.awd.model.SchoolMember;

public class DbCheck {
    public static void main(String[] args) throws Exception {
        Connection c = SingletonConnection.getConnection();
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT email, role FROM members ORDER BY id")) {
            while (rs.next()) {
                System.out.println(rs.getString("email") + " -> "
                        + rs.getString("role"));
            }
        }

        System.out.println("--- findAll() via DAO ---");
        List<SchoolMember> all = MemberDaoJdbc.getInstance().findAll();
        for (SchoolMember m : all) {
            System.out.println(m.getFullName() + " [" +
                    m.getClass().getSimpleName() + "]");
        }
    }
}