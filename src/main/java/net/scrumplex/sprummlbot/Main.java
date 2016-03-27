package net.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import net.scrumplex.sprummlbot.tools.Exceptions;
import net.scrumplex.sprummlbot.webinterface.WebServerManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {

    static long startTime = 0;

    public static void main(final String[] args) throws InterruptedException, IOException {
        startTime = System.currentTimeMillis();
        Startup.start();
        setUncaughtExceptionHandlerForThreads();
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
                    Vars.SPRUMMLBOT_STATUS = net.scrumplex.sprummlbot.State.STOPPING;
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

    static void setUncaughtExceptionHandlerForThreads() {
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    Exceptions.handle(e, "Uncaught Exception occurred!", false);
                }
            });
        }
    }
}