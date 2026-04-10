package app;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorUtils {

    public static String getErrorDetail(Throwable ex) {
        if (ex == null) {
            return "No additional error detail available.";
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
}