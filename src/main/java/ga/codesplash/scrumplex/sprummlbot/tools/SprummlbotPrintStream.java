package ga.codesplash.scrumplex.sprummlbot.tools;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * System.out edit
 */
public class SprummlbotPrintStream extends PrintStream {

    public SprummlbotPrintStream() {
        super(System.out);
    }

    public static final StringBuilder htmlLog = new StringBuilder();
    private final StringBuilder log = new StringBuilder();

    @Override
    public void println(String msg) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("d.M.Y HH:mm:ss");
        htmlLog.append("[").append(sdf.format(cal.getTime())).append("] ").append(msg).append("\n<br>\n");
        log.append("[").append(sdf.format(cal.getTime())).append("] ").append(msg).append("\n");
        super.println("[" + sdf.format(cal.getTime()) + "] " + msg);
    }

    /**
     * @return returns full console log with \n separators
     */
    public String getLog() {
        return log.toString();
    }

    /**
     * @return returns full console log with <br> separators
     */
    public String getHTMLLog() {
        return htmlLog.toString();
    }
}
