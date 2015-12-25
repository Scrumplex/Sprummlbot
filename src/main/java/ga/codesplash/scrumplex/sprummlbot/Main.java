package ga.codesplash.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.api.VirtualServerProperty;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import ga.codesplash.scrumplex.sprummlbot.configurations.Configuration;
import ga.codesplash.scrumplex.sprummlbot.plugins.ClasspathLoader;
import ga.codesplash.scrumplex.sprummlbot.plugins.PluginLoader;
import ga.codesplash.scrumplex.sprummlbot.plugins.PluginManager;
import ga.codesplash.scrumplex.sprummlbot.tools.Exceptions;
import ga.codesplash.scrumplex.sprummlbot.tools.SprummlbotErrStream;
import ga.codesplash.scrumplex.sprummlbot.tools.SprummlbotOutStream;
import ga.codesplash.scrumplex.sprummlbot.vpn.VPNConfig;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Main class.
 * Loads config files and starts the services
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
     * @param args Arguments by the JVM
     */
    public static void main(String[] args) {
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
            Exceptions.handle(e, "Licenses File couldn't be created.");
        }
        /**
         * Defining Plugin Loader and Manager
         */
        pluginManager = new PluginManager();
        pluginLoader = new PluginLoader(pluginManager);
        classpathLoader = new ClasspathLoader();

        /**
         * Checking for cmd line args
         */
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("-setupConfigs")) {
                /**
                 * This sets the config files up.
                 */
                System.out.println("This will create all configs! [Y|n]");
                Scanner txt = new Scanner(System.in);
                while (true) {
                    String line = txt.nextLine();
                    if (line.equalsIgnoreCase("y") || line.equalsIgnoreCase("")) {
                        try {
                            File f = new File("config.ini");
                            Configuration.load(f);
                        } catch (IOException e) {
                            Exceptions.handle(e, "Unknown Setup Error");
                        }
                        txt.close();
                        System.exit(0);
                    }
                }
            }
        }

        /**
         * Loading Configurations
         */
        File f = new File("config.ini");
        System.out.println("Loading Config!");
        try {
            Configuration.load(f);
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
            System.out.println("Checking for updates!");
            updater = new Updater("https://raw.githubusercontent.com/Scrumplex/Sprummlbot/master/version.txt",
                    Vars.BUILD_ID);
            try {
                if (updater.isUpdateAvailable()) {
                    System.out.println("[UPDATER] UPDATE AVAILABLE!");
                    System.out.println("[UPDATER] Download here: https://sprum.ml/releases/latest");
                    Vars.UPDATE_AVAILABLE = true;
                }
            } catch (IOException updateException) {
                Exceptions.handle(updateException, "UPDATER ERROR", false);
            }
        }

        /**
         * Some outputs
         */
        System.out.println("Hello! Sprummlbot v" + Vars.VERSION + " is starting...");
        System.out.println("This Bot is powered by https://github.com/TheHolyWaffle/TeamSpeak-3-Java-API");
        System.out.println(
                "If Sprummlbot loses connection to server the bot will close itself! So please use a restart Script.");
        System.out.println("Please put the ip of your bot into your serverquerywhitelist!");
        System.out.println("");
        /**
         * Connecting to Server
         * See ga.codesplash.scrumplex.sprummlbot.Startup
         */
        try {
            Connect.init();
        } catch (Exception connectException) {
            Exceptions.handle(connectException, "Connection Error!");
        }
        /**
         * Interactive Banner
         */
        System.out.println("Initializing Interactive Banner...");
        banner = new InteractiveBanner(Vars.INTERACTIVEBANNER_FILE, Vars.INTERACTIVEBANNER_TIME_POS, Vars.INTERACTIVEBANNER_DATE_POS, Vars.INTERACTIVEBANNER_USERS_POS, Vars.INTERACTIVEBANNER_COLOR, Vars.INTERACTIVEBANNER_FONT_SIZE);
        Map<VirtualServerProperty, String> settings = new HashMap<>();
        settings.put(VirtualServerProperty.VIRTUALSERVER_HOSTBANNER_GFX_URL, Vars.SERVER + ":9911/f/banner.png");
        settings.put(VirtualServerProperty.VIRTUALSERVER_HOSTBANNER_GFX_INTERVAL, "60");
        Vars.API.editServer(settings);


        /**
         * Trying to start Webserver
         */
        if (Vars.WEBINTERFACE_PORT != 0) {
            try {
                WebServerManager.start();
            } catch (IOException e) {
                Exceptions.handle(e,
                        "Webinterface couldn't start. Port: " + Vars.WEBINTERFACE_PORT + " already bound?");
            }
            System.out.println("Started WebGUI on port " + Vars.WEBINTERFACE_PORT);
        }

        /**
         * This will load plugins in the ./plugins/ folder.
         * See ga.codesplash.scrumplex.sprummlbot.plugins.PluginLoader
         */
        System.out.println("Trying to load Plugins!");
        pluginLoader.loadAll();

        /**
         * Creating Shutdown Hook, to close the TS3 Connection and properly disabling plugins.
         */
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Disabling plugins...");
                pluginLoader.unLoadAll();
                System.out.println("Sprummlbot is shutting down!");
                try {
                    for (Client c : Vars.API.getClients().get()) {
                        if (Vars.NOTIFY.contains(c.getUniqueIdentifier())) {
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
         * Starts registering console commands
         * See ga.codesplash.scrumplex.sprummlbot.Console
         */
        Console.runReadThread();
        System.out.println("DONE!");
        System.out.println("Available Commands: list, stop");
        //TODO
        ServerOptimization.setServerBanner();
    }
}
