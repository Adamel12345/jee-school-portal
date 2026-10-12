package ma.uir.awd.dao;

import java.time.LocalDate;
import java.util.List;
import ma.uir.awd.model.AttendanceRecord;

public interface IAttendanceDao {
    // Records one student at one session; recording the same student, lab
    // and date again replaces the previous value.
    void record(String studentEmail, String labTitle, LocalDate sessionDate,
                boolean present);

    // Every session of this student, most recent first.
    List<AttendanceRecord> findByStudent(String studentEmail);

    // Percentage of sessions attended, between 0 and 100; 0 if none.
    double attendanceRate(String studentEmail);
}