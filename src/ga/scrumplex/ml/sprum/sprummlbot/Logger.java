package ga.scrumplex.ml.sprum.sprummlbot;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Logger {

	public static StringBuilder log = new StringBuilder();

	public static void out(String msg) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("d.M.Y HH:mm:ss");
		System.out.println("[" + sdf.format(cal.getTime()) + " | INFO] " + msg);
		log.append("[" + sdf.format(cal.getTime()) + " | INFO] " + msg + "\n<br>\n");
	}

	public static void warn(String msg) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("d.M.Y HH:mm:ss");
		System.out.println("[" + sdf.format(cal.getTime()) + " | WARN] " + msg);
		log.append("[" + sdf.format(cal.getTime()) + " | WARN] " + msg + "<br>");
	}
}