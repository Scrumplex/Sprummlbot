package ga.scrumplex.ml.sprum.sprummlbot.stuff;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;

import ga.scrumplex.ml.sprum.sprummlbot.Config;
import ga.scrumplex.ml.sprum.sprummlbot.Logger;

public class Exceptions {

	public static void handle(Exception e, String CAUSE) {
		handle(e, CAUSE, true);
	}
	
	public static void handle(Exception e, String CAUSE, boolean shutdown) {
		if(Config.debug == 2) {
			e.printStackTrace();
			System.exit(1);
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
			if(e instanceof ConfigException) {
				bw.write("\nCaused by config!");
			}
			if(e instanceof UnknownHostException) {
				bw.write("\nCaused by connection! Host: " + Config.server);
			}
			if(e instanceof IOException) {
				bw.write("\nIO Error!");
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
		if(shutdown) {
			System.exit(1);
		}
	}
}
