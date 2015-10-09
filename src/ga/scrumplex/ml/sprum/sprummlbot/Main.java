package ga.scrumplex.ml.sprum.sprummlbot;

import java.io.File;
import java.io.IOException;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import ga.scrumplex.ml.sprum.sprummlbot.bridge.TCPServer;
import ga.scrumplex.ml.sprum.sprummlbot.stuff.ConfigException;
import ga.scrumplex.ml.sprum.sprummlbot.stuff.Exceptions;
import ga.scrumplex.ml.sprum.sprummlbot.stuff.PermissionModifier;

public class Main extends Config {

	public static void main(String[] args) {

		File f = new File("config.ini");
		Logger.out("Loading Config!");
		if (f.exists() == false) {
			Exceptions.handle(new ConfigException("Config File doesn't exist!"), "Config Files doesnt exist!");
		}
		try {
			Configuration.load(f);
		} catch (Exception e) {
			Exceptions.handle(e, "CONFIG LOADING FAILED!");
		}

		if (Config.UPDATER_ENABLED) {
			Logger.out("Checking for updates!");
			Updater update = new Updater("http://data1.codesplash.ga/sprummlbot/updateversion.txt", Config.BUILDID);
			try {
				if (update.isupdateavailable()) {
					Logger.out("[UPDATER] UPDATE AVAILABLE!");
					for (int i = 0; i < 100; i++) {
						Logger.out(
								"                                                                                                                                         :p");
					}
					Logger.out("[UPDATER] Download here: https://github.com/Scrumplex/Sprummlbot");
				}
			} catch (Exception e1) {
				Exceptions.handle(e1, "UPDATER ERROR", false);
			}
		}

		Logger.out("Hello! Sprummlbot v" + Config.VERSION + " is starting...");
		Logger.out("");
		try {
			Startup.start();
		} catch (Exception e2) {
			Exceptions.handle(e2, "Connection Error!");
		}
		if (Config.PORT_WI != 0) {
			try {
				WebGUI.start();
			} catch (IOException e) {
				Exceptions.handle(e, "Webinterface couldn't start. Port: " + Config.PORT_WI + " already bound?");
			}
			Logger.out("Started WebGUI on port " + Config.PORT_WI);
		}

		if (Config.BRIDGE_ENABLED) {
			Logger.out("Starting TCP Bridge API!");
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						TCPServer.start();
					} catch (IOException e) {
						Exceptions.handle(e, "TCP Bridge failed to run!");
					}
				}
			});
			t.start();
		}

		Logger.out("Lastly changing ServerQuery Rights");
		PermissionModifier.allow();
		Logger.out("DONE!");
		Logger.out("Available Commands: list, stop");
		for (Client c : API.getClients()) {
			if (TEAM.contains(c.getUniqueIdentifier())) {
				API.sendPrivateMessage(c.getId(), "Sprummlbot is running!");
			}
		}
		Console.runReadThread();
	}
}
