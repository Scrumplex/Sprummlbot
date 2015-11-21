package ga.codesplash.scrumplex.sprummlbot;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import ga.codesplash.scrumplex.sprummlbot.configurations.Broadcasts;
import ga.codesplash.scrumplex.sprummlbot.configurations.Clients;
import ga.codesplash.scrumplex.sprummlbot.configurations.Configuration;
import ga.codesplash.scrumplex.sprummlbot.configurations.Messages;
import ga.codesplash.scrumplex.sprummlbot.plugins.PluginLoader;
import ga.codesplash.scrumplex.sprummlbot.stuff.ConfigException;
import ga.codesplash.scrumplex.sprummlbot.stuff.CustomOutputStream;
import ga.codesplash.scrumplex.sprummlbot.stuff.Exceptions;

public class SprummlbotLoader {

	public static PluginLoader pl = null;

    /**
     * Main method
     * @param args
     * Arguments by the JVM
     */
	public static void main(String[] args) {

		System.setOut(new CustomOutputStream(System.out));

		pl = new PluginLoader();

		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("-setupConfigs")) {
				System.out.println("Are you sure? This will delete old configs, if they exist! [Y|n]");
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
		System.out.println("Loading Config!");
		try {
			Configuration.load(f);
		} catch (Exception e) {
			Exceptions.handle(e, "CONFIG LOADING FAILED!");
		}

		if (Vars.UPDATE_ENABLED) {
			System.out.println("Checking for updates!");
			Updater update = new Updater("https://raw.githubusercontent.com/Scrumplex/Sprummlbot/master/version.txt",
					Vars.BUILDID);
			try {
				if (update.isupdateavailable()) {
					System.out.println("[UPDATER] UPDATE AVAILABLE!");
					System.out.println("[UPDATER] Download here: https://github.com/Scrumplex/Sprummlbot");
					Vars.UPDATE_AVAILABLE = true;
				}
			} catch (Exception e1) {
				Exceptions.handle(e1, "UPDATER ERROR", false);
			}
		}

		System.out.println("Hello! Sprummlbot v" + Vars.VERSION + " is starting...");
		System.out.println("This Bot is powered by https://github.com/TheHolyWaffle/TeamSpeak-3-Java-API");
		System.out.println(
				"If Sprummlbot loses connection to server the bot will close itself! So please use a restart Script.");
		System.out.println("Please put the ip of your bot into your serverquerywhitelist!");
		System.out.println("");
		try {
			Startup.start();
		} catch (Exception e2) {
			Exceptions.handle(e2, "Connection Error!");
		}
		if (Vars.WEBINTERFACE_PORT != 0) {
			try {
				WebGUI.start();
			} catch (IOException e) {
				Exceptions.handle(e,
						"Webinterface couldn't start. Port: " + Vars.WEBINTERFACE_PORT + " already bound?");
			}
			System.out.println("Started WebGUI on port " + Vars.WEBINTERFACE_PORT);
		}


		System.out.println("Trying to load Plugins!");
		pl.loadAll();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Disabling plugins...");
				pl.unloadAll();
				System.out.println("Sprummlbot is shutting down!");
				for (Client c : Vars.API.getClients()) {
					if (Vars.NOTIFY.contains(c.getUniqueIdentifier())) {
						Vars.API.sendPrivateMessage(c.getId(), "Sprummlbot is shutting down!");
					}
				}
				Vars.QUERY.exit();
			}
		});
		System.out.println("DONE!");
		System.out.println("Available Commands: list, stop");
		for (Client c : Vars.API.getClients()) {
			if (Vars.NOTIFY.contains(c.getUniqueIdentifier())) {
				Vars.API.sendPrivateMessage(c.getId(), "Sprummlbot is running!");
			}
		}
		Console.runReadThread();
	}
}
