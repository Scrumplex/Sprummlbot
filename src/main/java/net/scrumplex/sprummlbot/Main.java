package net.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import net.scrumplex.sprummlbot.tools.EasyMethods;
import net.scrumplex.sprummlbot.tools.Exceptions;
import net.scrumplex.sprummlbot.tools.SprummlbotErrStream;
import net.scrumplex.sprummlbot.tools.SprummlbotOutStream;
import net.scrumplex.sprummlbot.webinterface.WebServerManager;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {

    static long startTime = 0;

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
                    Tasks.service.schedule(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Shutdown takes too long! Forcing shutdown...");
                            for (Thread t : Thread.getAllStackTraces().keySet()) {
                                if (t != Thread.currentThread() && t.isAlive()) {
                                    System.out.println("Force killing " + t.getName() + "!");
                                    t.interrupt();
                                }
                            }
                        }
                    }, 8, TimeUnit.SECONDS);
                    Vars.SPRUMMLBOT_STATUS = net.scrumplex.sprummlbot.wrapper.State.STOPPING;
                    System.out.println("Shutting down Sprummlbot...");
                    Startup.pluginLoader.unLoadAll();
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
    }

    private static void createLicensesFile() throws IOException {
        File f = new File("licenses.txt");
        String sb = "Open Source Licenses:\n" +
                "Teamspeak-3-Java-API: http://www.gnu.org/licenses/gpl-3.0.en.html\n" +
                "JSON: http://www.json.org/license.html\n";
        EasyMethods.writeToFile(f, sb);
    }
}