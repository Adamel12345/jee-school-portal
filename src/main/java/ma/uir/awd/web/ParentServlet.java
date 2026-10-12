package ma.uir.awd.web;

import java.io.IOException;
import java.io.PrintWriter;
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
import ma.uir.awd.model.AttendanceRecord;
import ma.uir.awd.model.Parent;
import ma.uir.awd.model.SchoolMember;

@WebServlet("/parent/attendance")
public class ParentServlet extends HttpServlet {

    private final IMemberDao memberDao = MemberDaoJdbc.getInstance();
    private final IAttendanceDao attendanceDao = AttendanceDaoJdbc.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        SchoolMember member = (session == null)
                ? null
                : (SchoolMember) session.getAttribute("member");

        if (member == null) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }
        if (!(member instanceof Parent)) {
            response.sendError(403);
            return;
        }

        Parent parent = (Parent) member;
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        HtmlPage.open(request, out, "My children's attendance");

        for (String childEmail : parent.getChildren()) {
            SchoolMember child = memberDao.findByEmail(childEmail);
            if (child == null) continue;

            double rate = attendanceDao.attendanceRate(childEmail);
            String rateStr = String.format("%.0f%%", rate);

            out.println("<h2>" + child.getFullName() + " — " + rateStr + "</h2>");

            List<AttendanceRecord> records = attendanceDao.findByStudent(childEmail);
            if (records.isEmpty()) {
                out.println("<p>No sessions recorded yet.</p>");
            } else {
                out.println("<table>");
                out.println("<tr><th>Date</th><th>Lab</th><th>Status</th></tr>");
                for (AttendanceRecord r : records) {
                    out.println("<tr>");
                    out.println("<td>" + r.getSessionDate() + "</td>");
                    out.println("<td>" + r.getLabTitle() + "</td>");
                    out.println("<td>" + (r.isPresent() ? "Present" : "Absent") + "</td>");
                    out.println("</tr>");
                }
                out.println("</table>");
            }
        }

        out.println("<p><a href=\"" + request.getContextPath()
                + "/dashboard\">← Back to dashboard</a></p>");
        HtmlPage.close(out);
    }
}