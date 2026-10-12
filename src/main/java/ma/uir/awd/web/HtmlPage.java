package ma.uir.awd.web;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;

/** Opening and closing of every generated page, written once. */
public final class HtmlPage {

    private HtmlPage() { }  // only static methods: never instantiated

    public static void open(HttpServletRequest request, PrintWriter out, String title) {
        String css = request.getContextPath() + "/css/style.css";
        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"en\"><head><meta charset=\"UTF-8\">");
        out.println("<title>" + title + "</title>");
        out.println("<link rel=\"stylesheet\" href=\"" + css + "\">");
        out.println("</head><body><main>");
        out.println("<h1>" + title + "</h1>");
    }

    public static void close(PrintWriter out) {
        out.println("</main></body></html>");
    }
}