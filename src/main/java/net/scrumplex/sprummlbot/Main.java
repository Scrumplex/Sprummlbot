package net.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import net.scrumplex.sprummlbot.core.SprummlbotErrStream;
import net.scrumplex.sprummlbot.core.SprummlbotOutStream;
import net.scrumplex.sprummlbot.tools.EasyMethods;
import net.scrumplex.sprummlbot.tools.Exceptions;
import net.scrumplex.sprummlbot.webinterface.WebServerManager;
import net.scrumplex.sprummlbot.wrapper.PermissionGroup;
import net.scrumplex.sprummlbot.wrapper.State;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class Main {

    private static long startTime = 0;

    public static void main(final String[] args) {
        if (startTime != 0)
            return;
        startTime = System.currentTimeMillis();
        System.setOut(new SprummlbotOutStream());
        System.setErr(new SprummlbotErrStream());
        try {
            File f = new File("legal.txt");
            EasyMethods.writeByteArrayToFile(f, EasyMethods.convertStreamToByteArray(Main.class.getResourceAsStream("/legal.txt")));
        } catch (IOException ex) {
            Exceptions.handle(ex, "Licenses File couldn't be created.", true);
        }
        Startup.start();
        Runtime.getRuntime().addShutdownHook(new Thread(Main::cleanup));
        System.out.println("[Core] Sprummlbot has been started successfully in " + (new DecimalFormat("0.00").format((double) (System.currentTimeMillis() - Main.startTime) / 1000)) + " seconds.");
        System.out.println("Available console commands: login, reloadplugins");
        System.out.println("To stop this bot you can use these console commands: stop, exit, quit");
    }

    public static void shutdown(int code) {
        cleanup();
        System.exit(code);
    }

    private static void cleanup() {
        if (Sprummlbot.getSprummlbot().getSprummlbotState() == State.STOPPING)
            return;
        System.out.println("Cleaning up...");
        try {
            Sprummlbot sprummlbot = Sprummlbot.getSprummlbot();
            Sprummlbot.getSprummlbot().setSprummlbotState(State.STOPPING);
            System.out.println("Shutting down Sprummlbot...");
            WebServerManager.stop();

            sprummlbot.getPluginManager().unloadAll();
            sprummlbot.getModuleManager().stopAllModules();
            Vars.EXECUTOR.shutdownNow();
            Vars.SERVICE.shutdownNow();
            sprummlbot.getSyncAPI().unregisterAllEvents();
            for (Client c : sprummlbot.getSyncAPI().getClients()) {
                if (PermissionGroup.getPermissionGroupForField("notify").isPermitted(c.getUniqueIdentifier()) == PermissionGroup.Permission.PERMITTED) {
                    sprummlbot.getSyncAPI().sendPrivateMessage(c.getId(), "Sprummlbot is shutting down...");
                }
            }
        } catch (Throwable ignored) {
        }
        try {
            Sprummlbot.getSprummlbot().getTS3Connection().getQuery().exit();
        } catch (Exception ignored) {
        }
    }
}