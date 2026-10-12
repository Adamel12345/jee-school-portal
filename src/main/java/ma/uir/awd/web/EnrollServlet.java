package ma.uir.awd.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import ma.uir.awd.dao.IMemberDao;
import ma.uir.awd.dao.MemberDaoJdbc;
import ma.uir.awd.model.SchoolMember;
import ma.uir.awd.model.Student;

@WebServlet("/student/enroll")
public class EnrollServlet extends HttpServlet {

    private final IMemberDao dao = MemberDaoJdbc.getInstance();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        SchoolMember member = (session == null)
                ? null
                : (SchoolMember) session.getAttribute("member");

        if (member == null) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        if (!(member instanceof Student)) {
            response.sendError(403);
            return;
        }

        Student s = (Student) member;
        String course = request.getParameter("course");
        s.enroll(course);

        dao.save(s);   // ← AJOUT IMPORTANT

        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}