package ma.uir.awd.web;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import ma.uir.awd.model.Admin;
import ma.uir.awd.model.Parent;
import ma.uir.awd.model.SchoolMember;
import ma.uir.awd.model.Student;
import ma.uir.awd.model.Teacher;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        SchoolMember member = (session == null)
                ? null
                : (SchoolMember) session.getAttribute("member");

        if (member == null) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        HtmlPage.open(request, out, "Welcome, " + member.getFullName());

        if (member instanceof Student) {
            Student s = (Student) member;
            out.println("<h2>My courses</h2>");
            out.println("<table><tr><th>Course</th></tr>");
            for (String c : s.getCourses()) {
                out.println("<tr><td>" + c + "</td></tr>");
            }
            out.println("</table>");
            out.println("<p><a href=\"enroll.html\" class=\"btn btn-primary\">Enroll in a course</a></p>");

        } else if (member instanceof Teacher) {
            Teacher t = (Teacher) member;
            out.println("<h2>My subjects</h2>");
            out.println("<table><tr><th>Subject</th></tr>");
            for (String s : t.getSubjects()) {
                out.println("<tr><td>" + s + "</td></tr>");
            }
            out.println("</table>");
            out.println("<p><a href=\"assign.html\" class=\"btn btn-primary\">Add a subject</a></p>");
            out.println("<p><a href=\"teacher/attendance\" class=\"btn btn-secondary\">Take attendance</a></p>");

        } else if (member instanceof Parent) {
            Parent p = (Parent) member;
            out.println("<h2>My children</h2>");
            out.println("<table><tr><th>Email</th></tr>");
            for (String childEmail : p.getChildren()) {
                out.println("<tr><td>" + childEmail + "</td></tr>");
            }
            out.println("</table>");
            out.println("<p><a href=\"parent/attendance\" class=\"btn btn-primary\">Lab attendance</a></p>");

        } else if (member instanceof Admin) {
            out.println("<h2>Administration</h2>");
            out.println("<p><a href=\"admin/members\" class=\"btn btn-primary\">All members</a></p>");
        }

        out.println("<p><a href=\"logout\" class=\"btn btn-secondary\">Sign out</a></p>");
        HtmlPage.close(out);
    }
}