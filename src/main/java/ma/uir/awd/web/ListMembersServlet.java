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
import ma.uir.awd.dao.IMemberDao;
import ma.uir.awd.dao.MemberDaoJdbc;
import ma.uir.awd.model.Admin;
import ma.uir.awd.model.SchoolMember;

@WebServlet("/admin/members")
public class ListMembersServlet extends HttpServlet {

    private final IMemberDao dao = MemberDaoJdbc.getInstance();

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

        if (!(member instanceof Admin)) {
            response.sendError(403);
            return;
        }

        List<SchoolMember> all = dao.findAll();

        HtmlPage.open(request, out, "All members");
        out.println("<table>");
        out.println("<tr><th>Full name</th><th>Email</th><th>Profile</th><th></th></tr>");

        for (SchoolMember m : all) {
            String profile = m.getClass().getSimpleName();
            out.println("<tr>");
            out.println("<td>" + m.getFullName() + "</td>");
            out.println("<td>" + m.getEmail() + "</td>");
            out.println("<td>" + profile + "</td>");
            out.println("<td>");
            out.println("<form action=\"delete\" method=\"post\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + m.getEmail() + "\">");
            out.println("<button type=\"submit\" class=\"btn btn-secondary\">Delete</button>");
            out.println("</form>");
            out.println("</td>");
            out.println("</tr>");
        }

        out.println("</table>");
        out.println("<p><a href=\"" + request.getContextPath() + "/dashboard\">← Back to dashboard</a></p>");
        HtmlPage.close(out);
    }
}