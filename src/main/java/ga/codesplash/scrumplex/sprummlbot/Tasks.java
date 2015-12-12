package ga.codesplash.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.CommandFuture;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import ga.codesplash.scrumplex.sprummlbot.configurations.Messages;
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
        service = Executors.newScheduledThreadPool(1);
    }

    /**
     * Starts default Service
     */
    public static void startService() {

        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                final TS3ApiAsync api = Vars.API;
                try {
                    if (Vars.DEBUG == 2) {
                        System.out.println("Checking for Supports/AFKs... | Disable this message with debug=0");
                    }

                    for (String uid : Vars.IN_AFK.keySet()) {
                        if (api.getClientByUId(uid) == null) {
                            Vars.IN_AFK.remove(uid);
                            System.out.println("AFK Not there anymore: " + api.getDatabaseClientByUId(uid).get().getNickname());
                        }
                    }
                    api.getClients().onSuccess(new CommandFuture.SuccessListener<List<Client>>() {
                        @Override
                        public void handleSuccess(List<Client> result) {

                            for (Client c : result) {
                                String uid = c.getUniqueIdentifier();
                                int cid = c.getId();
                                int dbid = c.getDatabaseId();

                                for (int group : Vars.GROUPPROTECT_LIST.keySet()) {
                                    if (Vars.GROUPPROTECT_LIST.get(group).contains(uid)) {
                                        if (!ArrayUtils.contains(c.getServerGroups(), group)) {
                                            api.addClientToServerGroup(group, dbid);
                                        }
                                    } else {
                                        if (ArrayUtils.contains(c.getServerGroups(), group)) {
                                            api.removeClientFromServerGroup(group, dbid);
                                        }
                                    }
                                }

                                // AntiRec
                                if (Vars.ANTIREC_ENABLED) {
                                    if (c.isRecording()) {
                                        if (!Vars.ANTIREC_WHITELIST.contains(uid)) {
                                            System.out.println("RECORD: " + c.getNickname());
                                            api.pokeClient(cid, Messages.get("you-mustnt-record-here"));
                                            api.kickClientFromServer(Messages.get("you-mustnt-record-here"), cid);
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
                                                        if (!Vars.AFK_ALLOWED.contains(uid)) {
                                                            Vars.IN_AFK.put(uid, c.getChannelId());
                                                            api.moveClient(cid, Vars.AFK_CHANNEL_ID);
                                                            api.sendPrivateMessage(cid, Messages.get("you-were-moved-to-afk"));
                                                            System.out.println("AFK: " + c.getNickname());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (!c.isInputMuted() && c.isInputHardware()) {
                                        if (c.getIdleTime() < Vars.AFK_TIME) {
                                            if (Vars.IN_AFK.containsKey(uid)) {
                                                api.moveClient(cid, Vars.IN_AFK.get(uid));
                                                Vars.IN_AFK.remove(uid);
                                                api.sendPrivateMessage(cid, Messages.get("you-were-moved-back-from-afk"));
                                                System.out.println("Back again: " + c.getNickname());
                                            }
                                        }
                                    }
                                }

                                // Support
                                if (Vars.SUPPORT_ENABLED) {
                                    if (c.getChannelId() == Vars.SUPPORT_CHANNEL_ID) {
                                        if (!Vars.IN_SUPPORT.contains(uid)) {
                                            api.sendPrivateMessage(cid, Messages.get("you-joined-support-channel"));
                                            Vars.IN_SUPPORT.add(uid);
                                            System.out.println("Support: " + c.getNickname());
                                            api.getClients().onSuccess(new CommandFuture.SuccessListener<List<Client>>() {
                                                @Override
                                                public void handleSuccess(List<Client> result) {
                                                    for (Client user : result) {
                                                        if (Vars.SUPPORTERS.contains(user.getUniqueIdentifier())) {
                                                            api.sendPrivateMessage(user.getId(), Messages.get("someone-is-in-support"));
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }

                                    if (c.getChannelId() != Vars.SUPPORT_CHANNEL_ID) {
                                        if (Vars.IN_SUPPORT.contains(uid)) {
                                            api.sendPrivateMessage(cid, Messages.get("you-are-not-longer-in-support-queue"));
                                            Vars.IN_SUPPORT.remove(uid);
                                            System.out.println("Not Support: " + c.getNickname());
                                        }
                                    }
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    System.out.println("Timeout!");
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
                        System.out.println("Sending Broadcast...");
                        Vars.API.getClients().onSuccess(new CommandFuture.SuccessListener<List<Client>>() {
                        @Override
                        public void handleSuccess(List<Client> result) {
                            for (Client c : result) {
                                if (!Vars.BROADCAST_IGNORE.contains(c.getUniqueIdentifier()))
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

    /**
     * Starts Keep Alive Service
     */
    public static void startKeepAlive() {
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    Vars.UPDATE_AVAILABLE = Main.updater.isUpdateAvailable();
                } catch (IOException e) {
                }
                if (Vars.UPDATE_AVAILABLE) {
                    System.out.println("[UPDATER] UPDATE AVAILABLE!");
                    System.out.println("[UPDATER] Download here: https://sprum.ml/releases/latest");
                }
                if (Vars.DEBUG == 2)
                    System.out.println("Checking for connection...");
                if (Vars.API.whoAmI().isFailed()) {
                    System.out.println("Sprummlbot lost connection to server!");
                    System.exit(1);
                }
            }
        }, 0, 15, TimeUnit.SECONDS);
    }

}
