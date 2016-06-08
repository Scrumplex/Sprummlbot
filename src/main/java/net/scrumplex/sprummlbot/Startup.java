package net.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import net.scrumplex.sprummlbot.configurations.Configuration;
import net.scrumplex.sprummlbot.configurations.Messages;
import net.scrumplex.sprummlbot.plugins.CommandHandler;
import net.scrumplex.sprummlbot.plugins.CommandManager;
import net.scrumplex.sprummlbot.plugins.PluginLoader;
import net.scrumplex.sprummlbot.plugins.PluginManager;
import net.scrumplex.sprummlbot.tools.EasyMethods;
import net.scrumplex.sprummlbot.tools.Exceptions;
import net.scrumplex.sprummlbot.tools.SprummlbotOutStream;
import net.scrumplex.sprummlbot.vpn.VPNConfig;
import net.scrumplex.sprummlbot.webinterface.FileLoader;
import net.scrumplex.sprummlbot.webinterface.WebServerManager;
import net.scrumplex.sprummlbot.wrapper.ChatCommand;
import net.scrumplex.sprummlbot.wrapper.CommandResponse;
import net.scrumplex.sprummlbot.wrapper.State;

import java.io.File;
import java.io.IOException;

public class Startup {

    public static PluginLoader pluginLoader;
    public static PluginManager pluginManager;
    public static SprummlbotOutStream out;
    public static VPNConfig vpnConfig;
    public static DynamicBanner banner;

    static void start() {
        pluginManager = new PluginManager();
        pluginLoader = new PluginLoader(pluginManager);

        File config = new File("config.ini");
        boolean firstStart = !config.exists();
        System.out.println("[Config] Loading Config...");
        try {
            Configuration.load(config, firstStart);
            if (firstStart) {
                System.out.println("[Config] Config files have been created! Please edit them!");
                Main.shutdown(0);
            }
        } catch (Exception e) {
            Exceptions.handle(e, "[Config] Config Loading Failed!");
        }

        SprummlbotCommands.registerCommands();

        if (Vars.UPDATE_ENABLED) {
            System.out.println("[Updater] Checking for updates...");
            Updater updater = new Updater();
            try {
                if (updater.isUpdateAvailable()) {
                    System.out.println("[Updater] UPDATE AVAILABLE!");
                    System.out.println("[Updater] Download here: https://sprum.ml/releases/latest");
                    Vars.UPDATE_AVAILABLE = true;
                }
            } catch (Exception updateException) {
                Exceptions.handle(updateException, "UPDATER ERROR", false);
            }
        }
        Vars.SPRUMMLBOT_STATUS = State.STARTING;

        if (Vars.VPNCHECKER_ENABLED && Vars.VPNCHECKER_SAVE) {
            System.out.println("[VPN Checker] Loading saved ip addresses...");
            try {
                vpnConfig = new VPNConfig(new File("vpnips.ini"));
            } catch (IOException e) {
                Exceptions.handle(e, "[VPN Checker] Saved ip addresses could not be loaded!", false);
            }
        }

        if (Vars.IP.equalsIgnoreCase("auto"))
            try {
                Vars.IP = EasyMethods.getPublicIP();
            } catch (IOException e) {
                Exceptions.handle(e, "Couldn't get public ip.");
            }
        System.out.println("[Internal] Public IP is " + Vars.IP);
        System.out.println("[Internal] Hello! Sprummlbot v" + Vars.VERSION + " is starting...");
        System.out.println("[Internal] This Bot is powered by https://github.com/TheHolyWaffle/TeamSpeak-3-Java-API");
        try {
            Connect.init();
        } catch (Exception connectException) {
            Exceptions.handle(connectException, "Connection Error!");
        }

        System.out.println("[Web Server] Initializing webinterface...");
        try {
            FileLoader.unpackDefaults();
        } catch (IOException e) {
            Exceptions.handle(e, "Couldn't unpack default webinterface");
        }
        System.out.println("[Web Server] Starting webinterface...");
        try {
            WebServerManager.start();
        } catch (IOException e) {
            Exceptions.handle(e, "Webinterface couldn't start");
        }
        System.out.println("[Web Server] Webinterface started! Try \"!login\" with Admin permissions or \"login\" in the console");

        pluginLoader.loadAll();

        Console.runReadThread();
        Tasks.startInternalRunner();
    }
}
