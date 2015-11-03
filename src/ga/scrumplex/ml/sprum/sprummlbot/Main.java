package ga.scrumplex.ml.sprum.sprummlbot;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.ini4j.InvalidFileFormatException;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import ga.scrumplex.ml.sprum.sprummlbot.Configurations.Broadcasts;
import ga.scrumplex.ml.sprum.sprummlbot.Configurations.Clients;
import ga.scrumplex.ml.sprum.sprummlbot.Configurations.Configuration;
import ga.scrumplex.ml.sprum.sprummlbot.Configurations.Messages;
import ga.scrumplex.ml.sprum.sprummlbot.bridge.TCPServer;
import ga.scrumplex.ml.sprum.sprummlbot.stuff.ConfigException;
import ga.scrumplex.ml.sprum.sprummlbot.stuff.Exceptions;

public class Main extends Config {

	public static void main(String[] args) {

		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("-setupConfigs")) {
				Logger.out("Are you sure? This will delete old configs, if they exist! [Y|n]");
				Scanner txt = new Scanner(System.in);
				if (!txt.nextLine().equalsIgnoreCase("n")) {
					File f = new File("config.ini");
					try {
						Configuration.updateCFG(f);
						f = new File("messages-en.ini");
						Messages.updateCFG(f);
						f = new File("messages-de.ini");
						Messages.updateCFG(f);
						f = new File("clients.ini");
						Clients.updateCFG(f);
						f = new File("broadcasts.ini");
						Broadcasts.updateCFG(f);
					} catch (IOException | ConfigException e) {
						Exceptions.handle(e, "Unknown Setup Error");
					}
				}
				txt.close();
				System.exit(0);
			}
		}

		File f = new File("config.ini");
		Logger.out("Loading Config!");
		try {
			Configuration.load(f);
		} catch (Exception e) {
			Exceptions.handle(e, "CONFIG LOADING FAILED!");
		}

		if (Config.UPDATER_ENABLED) {
			Logger.out("Checking for updates!");
			Updater update = new Updater("https://raw.githubusercontent.com/Scrumplex/Sprummlbot/master/version.txt",
					Config.BUILDID);
			try {
				if (update.isupdateavailable()) {
					Logger.out("[UPDATER] UPDATE AVAILABLE!");
					Logger.out("[UPDATER] Download here: https://github.com/Scrumplex/Sprummlbot");
				}
			} catch (Exception e1) {
				Exceptions.handle(e1, "UPDATER ERROR", false);
			}
		}

		Logger.out("Hello! Sprummlbot v" + Config.VERSION + " is starting...");
		Logger.out("This Bot is powered by https://github.com/TheHolyWaffle/TeamSpeak-3-Java-API");
		Logger.warn(
				"If Sprummlbot loses connection to server the bot will close itself! So please use a restart Script.");
		Logger.warn("Please put the ip of your bot into your serverquerywhitelist!");
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

		Logger.out("DONE!");
		Logger.out("Available Commands: list, stop");
		for (Client c : API.getClients()) {
			if (NOTIFY.contains(c.getUniqueIdentifier())) {
				API.sendPrivateMessage(c.getId(), "Sprummlbot is running!");
			}
		}
		Console.runReadThread();
		Logger.out("Starting Keep Alive Process");
		Tasks.startKeepAlive();
	}
}
