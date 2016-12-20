package net.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.TS3Config;
import net.scrumplex.sprummlbot.config.Configuration;
import net.scrumplex.sprummlbot.module.*;
import net.scrumplex.sprummlbot.plugins.CommandManager;
import net.scrumplex.sprummlbot.plugins.PluginManager;
import net.scrumplex.sprummlbot.service.MainService;
import net.scrumplex.sprummlbot.tools.EasyMethods;
import net.scrumplex.sprummlbot.tools.Exceptions;
import net.scrumplex.sprummlbot.vpn.VPNConfig;
import net.scrumplex.sprummlbot.webinterface.FileLoader;
import net.scrumplex.sprummlbot.webinterface.WebServerManager;
import net.scrumplex.sprummlbot.wrapper.State;

import java.io.File;
import java.io.IOException;

class Startup {


    static void start() {
        Sprummlbot sprummlbot = Sprummlbot.getSprummlbot();
        sprummlbot.setPluginManager(new PluginManager(sprummlbot));
        sprummlbot.setCommandManager(new CommandManager(sprummlbot));

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
        sprummlbot.setSprummlbotState(State.STARTING);

        if (Vars.VPNCHECKER_ENABLED && Vars.VPNCHECKER_SAVE) {
            System.out.println("[VPN Checker] Loading saved ip addresses...");
            try {
                Vars.VPNCONFIG = new VPNConfig(new File("vpnips.ini"));
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
        System.out.println("[Core] Public IP is " + Vars.IP);
        System.out.println("[Core] Hello! Sprummlbot v" + Vars.VERSION + " is starting...");
        System.out.println("[Core] This Bot is powered by https://github.com/TheHolyWaffle/TeamSpeak-3-Java-API");
        sprummlbot.setModuleManager(new ModuleManager());
        sprummlbot.setMainService(new MainService(Vars.TIMER_TICK));
        try {
            TS3Config ts3Config = new TS3Config();
            ts3Config.setHost(Vars.SERVER);
            ts3Config.setQueryPort(Vars.PORT_SQ);
            ts3Config.setFloodRate(Vars.FLOODRATE);
            TS3Connection ts3Connection = new TS3Connection(ts3Config, Vars.LOGIN[0], Vars.LOGIN[1], Vars.NICK, Vars.SERVER_ID);
            ts3Connection.initialize();
            sprummlbot.setTS3Connection(ts3Connection);
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

        System.out.println("[Core] Loading defaults...");


        SprummlbotCommands.registerCommands();
        try {
            sprummlbot.getModuleManager().registerModuleType(AFKMover.class, "afk_mover", null);
            sprummlbot.getModuleManager().registerModuleType(AntiRecordingMessage.class, "anti_rec", null);
            sprummlbot.getModuleManager().registerModuleType(ChannelNotifier.class, "channel_notifier", null);
            sprummlbot.getModuleManager().registerModuleType(JoinMessage.class, "join_msg", null);
            sprummlbot.getModuleManager().registerModuleType(ServerGroupProtector.class, "server_group_protector", null);
            sprummlbot.getModuleManager().registerModuleType(ChannelStats.class, "channel_stats", null);
        } catch (ModuleException e) {
            Exceptions.handle(e, "Could not register default Sprummlbot modules.");
        }

        sprummlbot.getPluginManager().loadAll();
        System.out.println("[Modules] Initializing modules...");

        File configDir = new File("configs");
        if (!configDir.exists())
            configDir.mkdir();
        File[] files = configDir.listFiles((dir, name) -> name.endsWith(".ini"));
        try {
            for (File f : files) {
                new ModuleConfiguration(f).findModules();
            }
        } catch (Exception ex) {
            Exceptions.handle(ex, "Could not load module configs");
        }
        sprummlbot.getModuleManager().startAllModules();

        sprummlbot.getMainService().start();
        Console.runReadThread();
    }
}
