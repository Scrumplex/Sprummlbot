package net.scrumplex.sprummlbot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.github.theholywaffle.teamspeak3.api.VirtualServerProperty;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import net.scrumplex.sprummlbot.configurations.Configuration;
import net.scrumplex.sprummlbot.plugins.ClasspathLoader;
import net.scrumplex.sprummlbot.plugins.PluginLoader;
import net.scrumplex.sprummlbot.plugins.PluginManager;
import net.scrumplex.sprummlbot.tools.EasyMethods;
import net.scrumplex.sprummlbot.tools.Exceptions;
import net.scrumplex.sprummlbot.tools.SprummlbotErrStream;
import net.scrumplex.sprummlbot.tools.SprummlbotOutStream;
import net.scrumplex.sprummlbot.vpn.VPNConfig;

/**
 * Main class. Loads config files and starts the services
 */
public class Main {

	public static Updater updater;
	public static PluginLoader pluginLoader;
	public static PluginManager pluginManager;
	public static SprummlbotOutStream out;
	public static ClasspathLoader classpathLoader;
	public static VPNConfig vpnConfig;
	public static InteractiveBanner banner;

	/**
	 * Main method
	 *
	 * @param args
	 *            Arguments by the JVM
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		Tasks.init();
		/**
		 * Modifying System.out
		 */
		out = new SprummlbotOutStream();
		System.setOut(out);
		System.setErr(new SprummlbotErrStream());

		/**
		 * Create / update licenses.txt
		 */
		try {
			FileManager.createLicensesFile();
		} catch (IOException e) {
			Exceptions.handle(e, "Licenses File couldn't be created.", false);
		}

		/**
		 * Defining Plugin Loader and Manager
		 */
		pluginManager = new PluginManager();
		pluginLoader = new PluginLoader(pluginManager);
		classpathLoader = new ClasspathLoader();

		/**
		 * Loading Configurations
		 */
		File config = new File("config.ini");
        boolean firstStart = !config.exists();
		System.out.println("Loading Config!");
		try {
			Configuration.load(config, firstStart);
		} catch (Exception e) {
			Exceptions.handle(e, "CONFIG LOADING FAILED!");
		}

		if (Vars.VPNCHECKER_ENABLED && Vars.VPNCHECKER_SAVE) {
			System.out.println("Loading VPN Checker List...");
			try {
				vpnConfig = new VPNConfig(new File("vpnips.ini"));
			} catch (IOException e) {
				Exceptions.handle(e, "VPN Checker Error", false);
			}
		}

		/**
		 * Checking for Updates
		 */
		if (Vars.UPDATE_ENABLED) {
            System.out.println("[Updater] Checking for updates!");
            updater = new Updater("https://raw.githubusercontent.com/Scrumplex/Sprummlbot/master/version.txt",
                    Vars.BUILD_ID);
            try {
                if (updater.isUpdateAvailable()) {
                    System.out.println("[Updater] UPDATE AVAILABLE!");
                    System.out.println("[Updater] Download here: https://sprum.ml/releases/latest");
                    Vars.UPDATE_AVAILABLE = true;
                }
            } catch (IOException updateException) {
                Exceptions.handle(updateException, "UPDATER ERROR", false);
            }
            Tasks.startUpdater();
        }
        Vars.SPRUMMLBOT_STATUS = State.STARTING;

		/**
		 * Some outputs
		 */
		System.out.println("Hello! Sprummlbot v" + Vars.VERSION + " is starting...");
		System.out.println("This Bot is powered by https://github.com/TheHolyWaffle/TeamSpeak-3-Java-API");
		System.out.println("Please put the ip of your bot into your serverquery-whitelist!");
		/**
		 * Connecting to Server See <reference>Connect.java</reference>
		 */
		try {
			Connect.init();
		} catch (Exception connectException) {
			Exceptions.handle(connectException, "Connection Error!");
		}
		/**
		 * Interactive Banner
		 */
		if (Vars.INTERACTIVEBANNER_ENABLED) {
			try {
				System.out.println("Initializing Interactive Banner...");
				if (!Vars.INTERACTIVEBANNER_FILE.exists())
					Exceptions.handle(new FileNotFoundException("Banner file doesnt exist"),
							"Banner File doesn't exist", true);
				banner = new InteractiveBanner(Vars.INTERACTIVEBANNER_FILE, Vars.INTERACTIVEBANNER_TIME_POS,
						Vars.INTERACTIVEBANNER_DATE_POS, Vars.INTERACTIVEBANNER_USERS_POS, Vars.INTERACTIVEBANNER_COLOR,
						Vars.INTERACTIVEBANNER_FONT_SIZE);
				Map<VirtualServerProperty, String> settings = new HashMap<>();
				String ip = EasyMethods.getPublicIP();
				settings.put(VirtualServerProperty.VIRTUALSERVER_HOSTBANNER_GFX_URL,
						"http://" + ip + ":9911/f/banner.png");
				settings.put(VirtualServerProperty.VIRTUALSERVER_HOSTBANNER_GFX_INTERVAL, "60");
				Vars.API.editServer(settings);
			} catch (IOException e) {
				Exceptions.handle(e, "Error while initializing Interactive Banner");
			}
		}

        /**
         * Start Webinterface
         */
        try {
            WebServerManager.start();
        } catch (IOException e) {
            Exceptions.handle(e, "Webinterface couldn't start");
        }

        /**
		 * This will load plugins in the ./plugins/ folder. See
		 * <reference>PluginLoader</reference>
		 */
		pluginLoader.loadAll();

		/**
		 * Creating Shutdown Hook, to close the TS3 Connection and properly
		 * disabling plugins.
		 */
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				pluginLoader.unLoadAll();
				System.out.println("Sprummlbot is shutting down!");
				try {
					for (Client c : Vars.API.getClients().get()) {
						if (Vars.PERMGROUPS.get(Vars.PERMGROUPASSIGNMENTS.get("notify"))
								.isClientInGroup(c.getUniqueIdentifier())) {
							Vars.API.sendPrivateMessage(c.getId(), "Sprummlbot is shutting down!");
						}
					}
				} catch (InterruptedException e) {
					System.out.println("Could not send shutdown message!");
				}
				Vars.QUERY.exit();
                WebServerManager.stop();
			}
		});
		/**
		 * Starts registering console commands See
		 * ga.codesplash.scrumplex.sprummlbot.Console
		 */
		Console.runReadThread();
		System.out.println("DONE!");
		System.out.println("Available Commands: stop");
	}
}
