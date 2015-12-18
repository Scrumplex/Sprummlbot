package ga.codesplash.scrumplex.sprummlbot.tools;

import ga.codesplash.scrumplex.sprummlbot.plugins.Sprummlbot;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * System.out edit
 */
public class SprummlbotErrStream extends PrintStream {

    public SprummlbotErrStream() {
        super(System.err);
    }

    @Override
    public void println(String msg) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("d.M.Y HH:mm:ss");
        Sprummlbot.getConsole().htmlLog.append("[").append(sdf.format(cal.getTime())).append(" | ERROR] ").append(msg).append("\n<br>\n");
        Sprummlbot.getConsole().log.append("[").append(sdf.format(cal.getTime())).append(" | ERROR] ").append(msg).append("\n");
        super.println("[" + sdf.format(cal.getTime()) + " | ERROR] " + msg);
    }

    /**
     * @return returns full console log with \n separators
     */
    public String getLog() {
        return Sprummlbot.getConsole().getLog();
    }

    /**
     * @return returns full console log with <br> separators
     */
    public String getHTMLLog() {
        return Sprummlbot.getConsole().getHTMLLog();
    }
}
