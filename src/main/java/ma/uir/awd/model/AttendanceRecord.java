package ma.uir.awd.model;

import java.time.LocalDate;

/** One line of a student's attendance: one lab session, present or absent. */
public class AttendanceRecord {

    private final String labTitle;
    private final LocalDate sessionDate;
    private final boolean present;

    public AttendanceRecord(String labTitle, LocalDate sessionDate,
                            boolean present) {
        this.labTitle = labTitle;
        this.sessionDate = sessionDate;
        this.present = present;
    }

    public String getLabTitle()        { return labTitle; }
    public LocalDate getSessionDate()  { return sessionDate; }
    public boolean isPresent()         { return present; }
}