package ma.uir.awd.model;

/** What every person of the school has in common. */
public abstract class SchoolMember {

    private final String fullName;
    private final String email;
    private final String password;

    protected SchoolMember(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    public String getFullName() { return fullName; }
    public String getEmail()    { return email; }

    public boolean checkPassword(String candidate) {
        return candidate != null && candidate.equals(this.password);
    }

    // Needed by the DAO to store the member. Nobody else should call it.
    public String getPassword() {
        return password;
    }
}
