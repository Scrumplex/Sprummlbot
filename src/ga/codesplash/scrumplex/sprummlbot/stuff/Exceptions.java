package ga.codesplash.scrumplex.sprummlbot.stuff;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import ga.codesplash.scrumplex.sprummlbot.Vars;
import ga.codesplash.scrumplex.sprummlbot.Logger;

public class Exceptions {

	public static void handle(Exception e, String CAUSE) {
		handle(e, CAUSE, true);
	}

	public static void handle(Exception e, String CAUSE, boolean shutdown) {
		if (Vars.DEBUG == 2) {
			e.printStackTrace();
			if(shutdown) {
				System.exit(1);
			}
			return;
		}
		File lasterror = new File("lasterror.log");
		Logger.warn(CAUSE + " More information in " + lasterror.getAbsolutePath());
		if (!lasterror.exists()) {
			try {
				lasterror.createNewFile();
			} catch (IOException e1) {
				Logger.warn("There was an problem while creating \"lasterror.log\" file! Printing Errors:");
				Logger.warn("//MAIN ERROR://");
				e.printStackTrace();
				Logger.warn("");
				Logger.warn("//WRITE ERROR://");
				e1.printStackTrace();
			}
		}
		FileWriter fw = null;
		try {
			fw = new FileWriter(lasterror.getAbsoluteFile());
		} catch (IOException e1) {
			Logger.warn("There was an problem while creating \"lasterror.log\" file! Printing Errors:");
			Logger.warn("//MAIN ERROR://");
			e.printStackTrace();
			Logger.warn("");
			Logger.warn("//WRITE ERROR://");
			e1.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(fw);
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			bw.write(sw.toString());
			if (e instanceof ConfigException) {
				bw.write("\nCaused by config!");
			}
			bw.write("\n\nPlease send this error to support with the config file!");
		} catch (IOException e1) {
			Logger.warn("There was an problem while creating \"lasterror.log\" file! Printing Errors:");
			Logger.warn("//MAIN ERROR://");
			e.printStackTrace();
			Logger.warn("");
			Logger.warn("//WRITE ERROR://");
			e1.printStackTrace();
		}
		try {
			bw.close();
		} catch (IOException e1) {
			Logger.warn("There was an problem while creating \"lasterror.log\" file! Printing Errors:");
			Logger.warn("//MAIN ERROR://");
			e.printStackTrace();
			Logger.warn("");
			Logger.warn("//WRITE ERROR://");
			e1.printStackTrace();
		}
		if (shutdown) {
			if(Vars.DEBUG == 1) {
				Logger.out("Waiting 3 seconds for shutdown. (disable with debug=0)");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
				}
			}
			System.exit(1);
		}
	}
}
