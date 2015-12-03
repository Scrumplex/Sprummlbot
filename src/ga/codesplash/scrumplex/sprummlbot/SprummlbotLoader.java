package ga.codesplash.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import ga.codesplash.scrumplex.sprummlbot.configurations.*;
import ga.codesplash.scrumplex.sprummlbot.plugins.PluginLoader;
import ga.codesplash.scrumplex.sprummlbot.plugins.PluginManager;
import ga.codesplash.scrumplex.sprummlbot.tools.CustomOutputStream;
import ga.codesplash.scrumplex.sprummlbot.tools.Exceptions;
import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Main class.
 * Loads config files and starts the services
 */
public class SprummlbotLoader {

    public static PluginLoader pl = null;
    public static PluginManager pm = null;

    /**
     * Main method
     *
     * @param args Arguments by the JVM
     */
    public static void main(String[] args) {

        /**
         * Modifying System.out
         */
        System.setOut(new CustomOutputStream());

        /**
         * Defining Plugin Loader and Manager
         */
        pm = new PluginManager();
        pl = new PluginLoader(pm);

        /**
         * Checking for cmd line args
         */
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("-setupConfigs")) {
                /**
                 * This sets the config files up.
                 */
                System.out.println("Are you sure? This will delete old configs, if they exist! [Y|n]");
                Scanner txt = new Scanner(System.in);
                while (true) {
                    String line = txt.nextLine();
                    if (line.equalsIgnoreCase("y") || line.equalsIgnoreCase("")) {
                        try {
                            File f = new File("config.ini");
                            if (!f.exists()) {
                                if (!f.createNewFile()) {
                                    System.out.println("Could not create " + f.getName());
                                }
                            }
                            Ini ini = new Ini(f);
                            Configuration.updateCFG(ini);
                            f = new File("messages-en.ini");
                            if (!f.exists()) {
                                if (!f.createNewFile()) {
                                    System.out.println("Could not create " + f.getName());
                                }
                            }
                            ini = new Ini(f);
                            Messages.updateCFG(ini, Language.EN);
                            f = new File("messages-de.ini");
                            if (!f.exists()) {
                                if (!f.createNewFile()) {
                                    System.out.println("Could not create " + f.getName());
                                }
                            }
                            ini = new Ini(f);
                            Messages.updateCFG(ini, Language.DE);
                            f = new File("clients.ini");
                            if (!f.exists()) {
                                if (!f.createNewFile()) {
                                    System.out.println("Could not create " + f.getName());
                                }
                            }
                            ini = new Ini(f);
                            Clients.updateCFG(ini);
                            f = new File("broadcasts.ini");
                            if (!f.exists()) {
                                if (!f.createNewFile()) {
                                    System.out.println("Could not create " + f.getName());
                                }
                            }
                            ini = new Ini(f);
                            Broadcasts.updateCFG(ini);
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
         * TODO: Writing Config API
         */
        File f = new File("config.ini");
        System.out.println("Loading Config!");
        try {
            Configuration.load(f);
        } catch (Exception e) {
            Exceptions.handle(e, "CONFIG LOADING FAILED!");
        }

        /**
         * Checking for Updates
         */
        if (Vars.UPDATE_ENABLED) {
            System.out.println("Checking for updates!");
            Updater update = new Updater("https://raw.githubusercontent.com/Scrumplex/Sprummlbot/master/version.txt",
                    Vars.BUILD_ID);
            try {
                if (update.isUpdateAvailable()) {
                    System.out.println("[UPDATER] UPDATE AVAILABLE!");
                    System.out.println("[UPDATER] Download here: https://github.com/Scrumplex/Sprummlbot");
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
            Startup.start();
        } catch (Exception connectException) {
            Exceptions.handle(connectException, "Connection Error!");
        }
        /**
         * Trying to start Webserver
         */
        if (Vars.WEBINTERFACE_PORT != 0) {
            try {
                WebGUI.start();
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
        pl.loadAll();
        /**
         * Creating Shutdown Hook, to close the TS3 Connection and properly disabling plugins.
         */
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Disabling plugins...");
                pl.unLoadAll();
                System.out.println("Sprummlbot is shutting down!");
                for (Client c : Vars.API.getClients()) {
                    if (Vars.NOTIFY.contains(c.getUniqueIdentifier())) {
                        Vars.API.sendPrivateMessage(c.getId(), "Sprummlbot is shutting down!");
                    }
                }
                Vars.QUERY.exit();
            }
        });
        /**
         * Starts registering console commands
         * See ga.codesplash.scrumplex.sprummlbot.Console
         */
        Console.runReadThread();
        System.out.println("DONE!");
        System.out.println("Available Commands: list, stop");
        /**
         * Sends message to the Clients
         */
        for (Client c : Vars.API.getClients()) {
            if (Vars.NOTIFY.contains(c.getUniqueIdentifier())) {
                Vars.API.sendPrivateMessage(c.getId(), "Sprummlbot is running!");
            }
        }
    }
}
