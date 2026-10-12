package ma.uir.awd.web;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ma.uir.awd.dao.IMemberDao;
import ma.uir.awd.dao.MemberDaoJdbc;
import ma.uir.awd.model.Admin;
import ma.uir.awd.model.Parent;
import ma.uir.awd.model.SchoolMember;
import ma.uir.awd.model.Student;
import ma.uir.awd.model.Teacher;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final IMemberDao dao = MemberDaoJdbc.getInstance();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        String fullName = request.getParameter("fullName");
        String email    = request.getParameter("email");
        String password = request.getParameter("password");
        String role     = request.getParameter("role");

        if (dao.findByEmail(email) != null) {
            HtmlPage.open(request, out, "Already registered");
            out.println("<p>This email is already registered.</p>");
            out.println("<p><a href=\"register.html\">Back to registration</a></p>");
            HtmlPage.close(out);
            return;
        }

        SchoolMember member;
        if ("teacher".equals(role)) {
            member = new Teacher(fullName, email, password);
        } else if ("parent".equals(role)) {
            // Read the child's email
            String childEmail = request.getParameter("childEmail");

            // The child must already be registered as a Student
            SchoolMember child = dao.findByEmail(childEmail);
            if (!(child instanceof Student)) {
                HtmlPage.open(request, out, "No student with this email");
                out.println("<p>No student with this email.</p>");
                out.println("<p><a href=\"register.html\">Back to registration</a></p>");
                HtmlPage.close(out);
                return;
            }

            Parent p = new Parent(fullName, email, password);
            p.followChild(childEmail);
            member = p;
        } else {
            member = new Student(fullName, email, password);
        }

        dao.save(member);

        HtmlPage.open(request, out, "Account created");
        out.println("<p>Welcome, " + fullName + "! Your account has been created.</p>");
        out.println("<p><a href=\"login.html\">Go to sign in</a></p>");
        HtmlPage.close(out);
    }
}