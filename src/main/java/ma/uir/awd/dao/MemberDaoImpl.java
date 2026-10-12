package ma.uir.awd.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import ma.uir.awd.model.SchoolMember;

public class MemberDaoImpl implements IMemberDao {

    // The one and only instance.
    // static: it belongs to the class itself, not to an object,
    // so there is exactly one for the whole application.
    // The JVM creates it once, when the class is loaded.
    private static final MemberDaoImpl INSTANCE = new MemberDaoImpl();

    private final Map<String, SchoolMember> members = new ConcurrentHashMap<>();

    // private: no other class can write "new MemberDaoImpl()" any more.
    private MemberDaoImpl() {
        System.out.println("[DAO #" + System.identityHashCode(this) + "] created");
    }

    public static MemberDaoImpl getInstance() {
        return INSTANCE;
    }

    @Override
    public void save(SchoolMember member) {
        members.put(member.getEmail(), member);
        System.out.println("[DAO #" + System.identityHashCode(this)
                + "] save " + member.getEmail()
                + " -> " + members.size() + " member(s)");
    }

    @Override
    public SchoolMember findByEmail(String email) {
        if (email == null) {
            return null;
        }
        SchoolMember m = members.get(email);
        System.out.println("[DAO #" + System.identityHashCode(this)
                + "] findByEmail " + email
                + " among " + members.size() + " member(s)");
        return m;
    }

    @Override
    public List<SchoolMember> findAll() {
        return new ArrayList<>(members.values());
    }

    @Override
    public boolean delete(String email) {
        if (email == null) {
            return false;
        }
        return members.remove(email) != null;
    }
}