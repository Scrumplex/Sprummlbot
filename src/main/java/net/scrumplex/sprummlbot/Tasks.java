package net.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.api.CommandFuture;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import net.scrumplex.sprummlbot.config.Messages;
import net.scrumplex.sprummlbot.tools.Exceptions;
import net.scrumplex.sprummlbot.vpn.VPNChecker;
import net.scrumplex.sprummlbot.webinterface.WebServerManager;
import net.scrumplex.sprummlbot.wrapper.PermissionGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

class Tasks {

    private static final List<ScheduledFuture<?>> tasks = new ArrayList<>();

    static void startVPNChecker() {
        tasks.add(Vars.SERVICE.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (Vars.VPNCHECKER_ENABLED) {
                    if (Vars.DEBUG > 1)
                        System.out.println("[VPN Checker] Checking server for VPNs.");
                    Sprummlbot.getSprummlbot().getDefaultAPI().getClients().onSuccess(new CommandFuture.SuccessListener<List<Client>>() {
                        @Override
                        public void handleSuccess(List<Client> result) {
                            for (Client c : result) {
                                if (c.isServerQueryClient())
                                    return;
                                VPNChecker check = new VPNChecker(c);
                                if (check.isBlocked()) {
                                    System.out.println("[VPN Checker] " + c.getNickname() + " was kicked. Blacklisted IP: " + c.getIp());
                                    Sprummlbot.getSprummlbot().getDefaultAPI().kickClientFromServer(Messages.get("you-are-using-vpn"), c.getId());
                                }
                            }
                        }
                    });
                }
            }

        }, 0, Vars.VPNCHECKER_INTERVAL, TimeUnit.SECONDS));
    }

    static void startInternalRunner() {
        tasks.add(Vars.SERVICE.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    if (new Updater().isUpdateAvailable()) {
                        System.out.println("[Updater] UPDATE AVAILABLE!");
                        System.out.println("[Updater] Download here: https://sprum.ml/releases/latest");
                        Vars.UPDATE_AVAILABLE = true;
                    }
                } catch (Exception updateException) {
                    Exceptions.handle(updateException, "UPDATER ERROR", false);
                }
                if (Vars.DEBUG >= 2)
                    System.out.println("Clearing Permission Group cache...");
                for (PermissionGroup group : Vars.PERMGROUPS.values()) {
                    group.clearCache();
                }
                System.out.println("[Web Server] Restarting web server for cleanup...");
                WebServerManager.stop();
                try {
                    WebServerManager.start();
                } catch (IOException e) {
                    Exceptions.handle(e, "Could not start web server!");
                }
            }
        }, 30, 30, TimeUnit.MINUTES));
    }

    static void stopAll() {
        for (ScheduledFuture<?> t : tasks) {
            t.cancel(true);
        }
    }
}
