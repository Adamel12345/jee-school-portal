package ma.uir.awd.web;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import ma.uir.awd.dao.IMemberDao;
import ma.uir.awd.dao.MemberDaoJdbc;
import ma.uir.awd.model.SchoolMember;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final IMemberDao dao = MemberDaoJdbc.getInstance();    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        // TODO 1: read email and password.
        String email    = request.getParameter("email");
        String password = request.getParameter("password");

        // TODO 2: look the member up with the DAO. If there is none, or if
        //         checkPassword fails, write a page "wrong email or
        //         password" with a link back to login.html, and stop.
        SchoolMember member = dao.findByEmail(email);

        if (member == null || !member.checkPassword(password)) {
            HtmlPage.open(request, out, "Sign in failed");
            out.println("<p>Wrong email or password.</p>");
            out.println("<p><a href=\"login.html\">Back to sign in</a></p>");
            HtmlPage.close(out);
            return;
        }

        // TODO 3: store the member in the session under the name "member",
        //         then redirect to the dashboard.
        HttpSession session = request.getSession();
        session.setAttribute("member", member);

        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}
