package ma.uir.awd.dao;

import java.util.List;
import ma.uir.awd.model.SchoolMember;

public interface IMemberDao {
    void save(SchoolMember member);          // insert, or replace same email
    SchoolMember findByEmail(String email);  // null if nobody has this email
    List<SchoolMember> findAll();            // all members
    boolean delete(String email);            // true if someone was removed
}
