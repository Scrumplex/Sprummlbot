package net.scrumplex.sprummlbot.tools;

import net.scrumplex.sprummlbot.Main;

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
        Main.out.htmlLog.append("[").append(sdf.format(cal.getTime())).append(" | ERROR] ").append(msg).append("\n<br>\n");
        Main.out.log.append("[").append(sdf.format(cal.getTime())).append(" | ERROR] ").append(msg).append("\n");
        super.println("[" + sdf.format(cal.getTime()) + " | ERROR] " + msg);
    }
}
