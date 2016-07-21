package net.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import net.scrumplex.sprummlbot.core.SprummlbotErrStream;
import net.scrumplex.sprummlbot.core.SprummlbotOutStream;
import net.scrumplex.sprummlbot.tools.EasyMethods;
import net.scrumplex.sprummlbot.tools.Exceptions;
import net.scrumplex.sprummlbot.webinterface.WebServerManager;
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
        Startup.out = new SprummlbotOutStream();
        System.setOut(Startup.out);
        System.setErr(new SprummlbotErrStream());
        try {
            createLicensesFile();
        } catch (IOException ex) {
            Exceptions.handle(ex, "Licenses File couldn't be created.", false);
        }
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Exceptions.handle(e, "An error occurred in thread " + t.getName(), false);
            }
        });
        Startup.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                cleanup();
            }
        });
        System.out.println("[Core]Done! It took " + (new DecimalFormat("0.00").format((double) (System.currentTimeMillis() - Main.startTime) / 1000)) + " seconds.");
        System.out.println("Available Console Commands: login, reloadplugins");
    }

    public static void shutdown(int code) {
        cleanup();
        System.exit(code);
    }

    private static void cleanup() {
        if (Vars.SPRUMMLBOT_STATUS == State.STOPPING)
            return;
        System.out.println("Cleaning up...");
        try {
            Sprummlbot sprummlbot = Sprummlbot.getSprummlbot();
            Vars.SPRUMMLBOT_STATUS = State.STOPPING;
            System.out.println("Shutting down Sprummlbot...");
            WebServerManager.stop();
            sprummlbot.getPluginManager().unloadAll();
            sprummlbot.getModuleManager().stopAllModules();
            Vars.EXECUTOR.shutdownNow();
            Vars.SERVICE.shutdownNow();
            sprummlbot.getSyncAPI().unregisterAllEvents();
            for (Client c : sprummlbot.getSyncAPI().getClients()) {
                if (Vars.PERMGROUPS.get(Vars.PERMGROUPASSIGNMENTS.get("notify"))
                        .isClientInGroup(c.getUniqueIdentifier())) {
                    sprummlbot.getSyncAPI().sendPrivateMessage(c.getId(), "Sprummlbot is shutting down...");
                }
            }
        } catch (Throwable ignored) {
        }
        try {
            Vars.QUERY.exit();
        } catch (Exception ignored) {
        }
    }

    private static void createLicensesFile() throws IOException {
        File f = new File("licenses.txt");
        String sb = "Open Source Licenses:\n" +
                "TEAMSPEAK-3-JAVA-API: https://github.com/TheHolyWaffle/TeamSpeak-3-Java-API/blob/master/LICENSE\n" +
                "JSON: http://www.json.org/license.html\n" +
                "GOOGLE ZXING: http://www.apache.org/licenses/LICENSE-2.0";
        EasyMethods.writeToFile(f, sb);
    }
}