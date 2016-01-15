package net.scrumplex.sprummlbot.tools;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * System.out edit
 */
public class SprummlbotOutStream extends PrintStream {

    final StringBuilder htmlLog = new StringBuilder();
    final StringBuilder log = new StringBuilder();

    public SprummlbotOutStream() {
        super(System.out);
    }

    @Override
    public void println(String msg) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("d.M.Y HH:mm:ss");
        htmlLog.append("[").append(sdf.format(cal.getTime())).append(" | INFO] ").append(msg).append("\n<br>\n");
        log.append("[").append(sdf.format(cal.getTime())).append(" | INFO] ").append(msg).append("\n");
        super.println("[" + sdf.format(cal.getTime()) + " | INFO] " + msg);
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
