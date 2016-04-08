package net.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import net.scrumplex.sprummlbot.tools.EasyMethods;
import net.scrumplex.sprummlbot.tools.Exceptions;
import net.scrumplex.sprummlbot.tools.SprummlbotErrStream;
import net.scrumplex.sprummlbot.tools.SprummlbotOutStream;
import net.scrumplex.sprummlbot.webinterface.WebServerManager;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class Main {

    private static long startTime = 0;

    public static void main(final String[] args) throws InterruptedException, IOException {
        startTime = System.currentTimeMillis();
        Startup.out = new SprummlbotOutStream();
        System.setOut(Startup.out);
        System.setErr(new SprummlbotErrStream());
        try {
            createLicensesFile();
        } catch (IOException e) {
            Exceptions.handle(e, "Licenses File couldn't be created.", false);
        }
        Startup.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    Vars.SPRUMMLBOT_STATUS = net.scrumplex.sprummlbot.wrapper.State.STOPPING;
                    System.out.println("Shutting down Sprummlbot...");
                    Startup.pluginLoader.unloadAll();
                    Vars.EXECUTOR.shutdownNow();
                    WebServerManager.stop();
                    Vars.API.unregisterAllEvents().get(1, TimeUnit.SECONDS);
                    for (Client c : Vars.API.getClients().get()) {
                        if (Vars.PERMGROUPS.get(Vars.PERMGROUPASSIGNMENTS.get("notify"))
                                .isClientInGroup(c.getUniqueIdentifier())) {
                            Vars.API.sendPrivateMessage(c.getId(), "Sprummlbot is shutting down...").getUninterruptibly();
                        }
                    }
                } catch (Throwable ignored) {
                }
                Vars.QUERY.exit();
            }
        });
        System.out.println("Done! It took " + (new DecimalFormat("#.00").format((double) (System.currentTimeMillis() - Main.startTime) / 1000)) + " seconds.");
        System.out.println("Available Console Commands: stop, login, reloadplugins");
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