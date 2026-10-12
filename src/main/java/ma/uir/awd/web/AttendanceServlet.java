package ma.uir.awd.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import ma.uir.awd.dao.AttendanceDaoJdbc;
import ma.uir.awd.dao.IAttendanceDao;
import ma.uir.awd.dao.IMemberDao;
import ma.uir.awd.dao.MemberDaoJdbc;
import ma.uir.awd.model.SchoolMember;
import ma.uir.awd.model.Student;
import ma.uir.awd.model.Teacher;

@WebServlet("/teacher/attendance")
public class AttendanceServlet extends HttpServlet {

    private final IMemberDao memberDao = MemberDaoJdbc.getInstance();
    private final IAttendanceDao attendanceDao = AttendanceDaoJdbc.getInstance();

    /** Helper: check the session, redirect/403 if needed. */
    private Teacher requireTeacher(HttpServletRequest request,
                                   HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        SchoolMember member = (session == null)
                ? null
                : (SchoolMember) session.getAttribute("member");

        if (member == null) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return null;
        }
        if (!(member instanceof Teacher)) {
            response.sendError(403);
            return null;
        }
        return (Teacher) member;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (requireTeacher(request, response) == null) return;

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        List<SchoolMember> all = memberDao.findAll();

        HtmlPage.open(request, out, "Take attendance");

        out.println("<form action=\"attendance\" method=\"post\">");
        out.println("<label for=\"labTitle\">Lab title</label>");
        out.println("<input type=\"text\" id=\"labTitle\" name=\"labTitle\" required>");

        out.println("<label for=\"sessionDate\">Session date</label>");
        out.println("<input type=\"date\" id=\"sessionDate\" name=\"sessionDate\" required>");

        out.println("<table>");
        out.println("<tr><th>Student</th><th>Present</th></tr>");

        for (SchoolMember m : all) {
            if (m instanceof Student) {
                out.println("<tr>");
                out.println("<td>" + m.getFullName() + "</td>");
                out.println("<td><input type=\"checkbox\" name=\"present\" value=\""
                        + m.getEmail() + "\"></td>");
                out.println("</tr>");
            }
        }
        out.println("</table>");

        out.println("<button type=\"submit\" class=\"btn btn-primary\">Save attendance</button>");
        out.println("</form>");

        out.println("<p><a href=\"" + request.getContextPath()
                + "/dashboard\">← Back to dashboard</a></p>");
        HtmlPage.close(out);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (requireTeacher(request, response) == null) return;

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String labTitle = request.getParameter("labTitle");
        LocalDate sessionDate = LocalDate.parse(request.getParameter("sessionDate"));

        // The browser sends only the CHECKED boxes.
        // getParameterValues returns null if none are ticked.
        String[] presentEmails = request.getParameterValues("present");
        List<String> presentList = (presentEmails == null)
                ? List.of()
                : Arrays.asList(presentEmails);

        // Go through ALL students, not through the parameters:
        // absent students are not in the parameters at all.
        int count = 0;
        for (SchoolMember m : memberDao.findAll()) {
            if (m instanceof Student) {
                boolean present = presentList.contains(m.getEmail());
                attendanceDao.record(m.getEmail(), labTitle, sessionDate, present);
                count++;
            }
        }

        PrintWriter out = response.getWriter();
        HtmlPage.open(request, out, "Attendance recorded");
        out.println("<p>Attendance recorded for " + count + " student(s).</p>");
        out.println("<p><a href=\"" + request.getContextPath()
                + "/dashboard\">← Back to dashboard</a></p>");
        HtmlPage.close(out);
    }
}