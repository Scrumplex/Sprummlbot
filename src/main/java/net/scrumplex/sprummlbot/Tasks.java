package net.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.api.CommandFuture;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import net.scrumplex.sprummlbot.configurations.Messages;
import net.scrumplex.sprummlbot.tools.Exceptions;
import net.scrumplex.sprummlbot.tools.PermissionGroup;
import net.scrumplex.sprummlbot.vpn.VPNChecker;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class is a class for some Tasks.
 * These will be started if they are enabled.
 */
class Tasks {

    private static ScheduledExecutorService service = null;

    /**
     * Initializes the ExecutorService
     */
    public static void init() {
        service = Executors.newScheduledThreadPool(3);
    }

    /**
     * Starts default Service
     */
    public static void startService() {

        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    if (Vars.DEBUG == 2) {
                        System.out.println("[Services] Checking for Events...");
                    }

                    for (String uid : Vars.IN_AFK.keySet()) {
                        if (Vars.API.getClientByUId(uid).isFailed()) {
                            Vars.IN_AFK.remove(uid);
                            System.out.println("[AFK Mover] " + Vars.API.getDatabaseClientByUId(uid).get().getNickname() + " was afk and left the server..");
                        }
                    }
                            Vars.API.getClients().onSuccess(new CommandFuture.SuccessListener<List<Client>>() {
                                @Override
                                public void handleSuccess(List<Client> result) {

                            for (Client c : result) {
                                String uid = c.getUniqueIdentifier();
                                int cid = c.getId();
                                int dbid = c.getDatabaseId();

                                for (int group : Vars.GROUPPROTECT_LIST.keySet()) {
                                    if (Vars.GROUPPROTECT_LIST.get(group).contains(uid)) {
                                        if (!ArrayUtils.contains(c.getServerGroups(), group)) {
                                            Vars.API.addClientToServerGroup(group, dbid);
                                        }
                                    } else {
                                        if (ArrayUtils.contains(c.getServerGroups(), group)) {
                                            Vars.API.removeClientFromServerGroup(group, dbid);
                                        }
                                    }
                                }

                                // AntiRec
                                if (Vars.ANTIREC_ENABLED) {
                                    if (c.isRecording()) {
                                        if (!Vars.PERMGROUPS.get(Vars.PERMGROUPASSIGNMENTS.get("antirec")).isClientInGroup(uid)) {
                                            System.out.println("[Anti Recording] " + c.getNickname() + " was kicked.");
                                            Vars.API.pokeClient(cid, Messages.get("you-mustnt-record-here"));
                                            Vars.API.kickClientFromServer(Messages.get("you-mustnt-record-here"), cid);
                                        }
                                    }
                                }

                                // AFK
                                if (Vars.AFK_ENABLED) {
                                    if (c.isInputMuted() || !c.isInputHardware()) {
                                        if (c.getIdleTime() >= Vars.AFK_TIME) {
                                            if (!Vars.IN_AFK.containsKey(uid)) {
                                                if (!Vars.AFKALLOWED.contains(c.getChannelId())) {
                                                    if (!c.getPlatform().equalsIgnoreCase("ServerQuery")) {
                                                        if (!Vars.PERMGROUPS.get(Vars.PERMGROUPASSIGNMENTS.get("afk")).isClientInGroup(uid)) {
                                                            Vars.IN_AFK.put(uid, c.getChannelId());
                                                            Vars.API.moveClient(cid, Vars.AFK_CHANNEL_ID);
                                                            Vars.API.sendPrivateMessage(cid, Messages.get("you-were-moved-to-afk"));
                                                            System.out.println("[AFK Mover] " + c.getNickname() + " is afk..");
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (!c.isInputMuted() && c.isInputHardware()) {
                                        if (c.getIdleTime() < Vars.AFK_TIME) {
                                            if (Vars.IN_AFK.containsKey(uid)) {
                                                Vars.API.moveClient(cid, Vars.IN_AFK.get(uid));
                                                Vars.IN_AFK.remove(uid);
                                                Vars.API.sendPrivateMessage(cid, Messages.get("you-were-moved-back-from-afk"));
                                                System.out.println("[AFK Mover] " + c.getNickname() + " is back again.");
                                            }
                                        }
                                    }
                                }

                                // Support
                                if (Vars.SUPPORT_ENABLED) {
                                    if (c.getChannelId() == Vars.SUPPORT_CHANNEL_ID) {
                                        if (!Vars.IN_SUPPORT.contains(uid)) {
                                            Vars.API.sendPrivateMessage(cid, Messages.get("you-joined-support-channel"));
                                            Vars.IN_SUPPORT.add(uid);
                                            System.out.println("[Support] " + c.getNickname() + " entered support room.");
                                            Vars.API.getClients().onSuccess(new CommandFuture.SuccessListener<List<Client>>() {
                                                @Override
                                                public void handleSuccess(List<Client> result) {
                                                    for (Client user : result) {
                                                        if (Vars.PERMGROUPS.get(Vars.PERMGROUPASSIGNMENTS.get("supporters")).isClientInGroup(user.getUniqueIdentifier())) {
                                                            Vars.API.sendPrivateMessage(user.getId(), Messages.get("someone-is-in-support"));
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }

                                    if (c.getChannelId() != Vars.SUPPORT_CHANNEL_ID) {
                                        if (Vars.IN_SUPPORT.contains(uid)) {
                                            Vars.API.sendPrivateMessage(cid, Messages.get("you-are-not-longer-in-support-queue"));
                                            Vars.IN_SUPPORT.remove(uid);
                                            System.out.println("[Support] " + c.getNickname() + " left support room.");
                                        }
                                    }
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    System.out.println("[Connection] Request Timeout.");
                }
            }
        }, 0, Vars.TIMER_TICK, TimeUnit.MILLISECONDS);
    }

    /**
     * Starts Broadcast Service
     */
    public static void startBroadCast() {
        service.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                try {
                    Random r = new Random();
                    final int i = r.nextInt(Vars.BROADCASTS.size());
                    if (Vars.DEBUG == 2)
                        System.out.println("[Broadcast] Sending...");
                    Vars.API.getClients().onSuccess(new CommandFuture.SuccessListener<List<Client>>() {
                        @Override
                        public void handleSuccess(List<Client> result) {
                            for (Client c : result) {
                                String groupName = Vars.PERMGROUPASSIGNMENTS.get("broadcast");
                                if (groupName == null) {
                                    System.err.println("[Broadcast] The group defined for Broadcast is not valid!");
                                    continue;
                                }
                                PermissionGroup group = Vars.PERMGROUPS.get(groupName);
                                if (group == null) {
                                    System.err.println("[Broadcast] The group " + groupName + " doesn't exist!");
                                    continue;
                                }
                                if (!group.isClientInGroup(c.getUniqueIdentifier()))
                                    Vars.API.sendPrivateMessage(c.getId(), Vars.BROADCASTS.get(i));
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, 0, Vars.BROADCAST_INTERVAL, TimeUnit.SECONDS);
    }

    public static void stopAll() {
        if (service.isShutdown())
            service.shutdownNow();
    }

    public static void startVPNChecker() {
        service.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (Vars.DEBUG > 1)
                    System.out.println("[VPN Checker] Checking server for VPNs.");
                Vars.API.getClients().onSuccess(new CommandFuture.SuccessListener<List<Client>>() {
                    @Override
                    public void handleSuccess(List<Client> result) {
                        for (Client c : result) {
                            VPNChecker check = new VPNChecker(c);
                            if (check.isBlocked()) {
                                System.out.println("[VPN Checker] " + c.getNickname() + " was kicked. VPN Type: " + check.getType() + " Blacklisted IP: " + c.getIp());
                                Vars.API.kickClientFromServer(Messages.get("you-are-using-vpn"), c.getId());
                            }
                        }
                    }
                });

            }

        }, 0, Vars.VPNCHECKER_INTERVAL, TimeUnit.SECONDS);
    }

    public static void startUpdater() {
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    if (Main.updater.isUpdateAvailable()) {
                        System.out.println("[Updater] UPDATE AVAILABLE!");
                        System.out.println("[Updater] Download here: https://sprum.ml/releases/latest");
                        Vars.UPDATE_AVAILABLE = true;
                    }
                } catch (IOException updateException) {
                    Exceptions.handle(updateException, "Updater Error", false);
                }
            }
        }, 0, 10, TimeUnit.MINUTES);
    }

}
