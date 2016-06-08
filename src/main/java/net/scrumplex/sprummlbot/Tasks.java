package net.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.CommandFuture;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.QueryError;
import net.scrumplex.sprummlbot.configurations.Messages;
import net.scrumplex.sprummlbot.tools.Exceptions;
import net.scrumplex.sprummlbot.vpn.VPNChecker;
import net.scrumplex.sprummlbot.webinterface.WebServerManager;
import net.scrumplex.sprummlbot.wrapper.PermissionGroup;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.scrumplex.sprummlbot.Vars.API;

class Tasks {

    private static final List<ScheduledFuture<?>> tasks = new ArrayList<>();

    static void startService() {
        tasks.add(Vars.SERVICE.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    if (Vars.DEBUG == 2)
                        System.out.println("[Services] Checking for Events...");

                    for (final int clid : Vars.IN_AFK.keySet()) {
                        API.getClientInfo(clid).onFailure(new CommandFuture.FailureListener() {
                            @Override
                            public void handleFailure(QueryError error) {
                                if (error.getId() == 512) {
                                    if (Vars.DEBUG == 2)
                                        System.out.println("[AFK Mover] Collected garbage!");
                                    Vars.IN_AFK.remove(clid);
                                }
                            }
                        });
                    }

                    for (final int clid : Vars.IN_SUPPORT) {
                        API.getClientInfo(clid).onFailure(new CommandFuture.FailureListener() {
                            @Override
                            public void handleFailure(QueryError error) {
                                if (error.getId() == 512) {
                                    if (Vars.DEBUG == 2)
                                        System.out.println("[Support Notifier] Collected garbage!");
                                    Vars.IN_SUPPORT.remove(new Integer(clid));
                                }
                            }
                        });
                    }

                    if (Vars.DYNBANNER_ENABLED) {
                        Vars.EXECUTOR.submit(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Vars.DYNBANNER_GEN = Startup.banner.getNewImageAsBytes();
                                } catch (Exception e) {
                                    Exceptions.handle(e, "Could not generate Dynamic Banner", false);
                                }
                            }
                        });
                    }

                    if (Vars.ANTIREC_ENABLED || Vars.AFK_ENABLED) {
                        API.getClients().onSuccess(new CommandFuture.SuccessListener<List<Client>>() {
                            @Override
                            public void handleSuccess(List<Client> result) {
                                for (Client c : result) {
                                    String uid = c.getUniqueIdentifier();
                                    final int clid = c.getId();
                                    int dbid = c.getDatabaseId();
                                    for (int group : Vars.GROUPPROTECT_LIST.keySet()) {
                                        if (Vars.GROUPPROTECT_LIST.get(group).contains(uid)) {
                                            if (!c.isInServerGroup(group))
                                                API.addClientToServerGroup(group, dbid);
                                        } else {
                                            if (c.isInServerGroup(group))
                                                API.removeClientFromServerGroup(group, dbid);
                                        }
                                    }

                                    // AntiRec
                                    if (Vars.ANTIREC_ENABLED) {
                                        if (c.isRecording()) {
                                            if (!Vars.PERMGROUPS.get(Vars.PERMGROUPASSIGNMENTS.get("antirec")).isClientInGroup(uid)) {
                                                System.out.println("[Anti Recording] " + c.getNickname() + " was kicked.");
                                                API.kickClientFromServer(Messages.get("you-mustnt-record-here"), clid);
                                            }
                                        }
                                    }

                                    // AFK
                                    if (Vars.AFK_ENABLED) {
                                        boolean isAway = c.isAway();
                                        boolean isMicMuted = c.isInputMuted();
                                        boolean isMicDisabled = !c.isInputHardware();
                                        boolean isSpeakerMuted = c.isOutputMuted();
                                        boolean isSpeakerDisabled = !c.isOutputHardware();

                                        int conditionMatch = 0;
                                        if (Vars.AFK_CONDITIONS.contains("away") && isAway)
                                            conditionMatch++;
                                        if (Vars.AFK_CONDITIONS.contains("mic-muted") && isMicMuted)
                                            conditionMatch++;
                                        if (Vars.AFK_CONDITIONS.contains("mic-disabled") && isMicDisabled)
                                            conditionMatch++;
                                        if (Vars.AFK_CONDITIONS.contains("speaker-muted") && isSpeakerMuted)
                                            conditionMatch++;
                                        if (Vars.AFK_CONDITIONS.contains("speaker-disabled") && isSpeakerDisabled)
                                            conditionMatch++;

                                        boolean isAfk = false;

                                        if ((Vars.AFK_CONDITIONS_MIN == -1) && (conditionMatch == Vars.AFK_CONDITIONS.size()))
                                            isAfk = true;
                                        else if (Vars.AFK_CONDITIONS_MIN <= conditionMatch)
                                            isAfk = true;

                                        if (!Vars.IN_AFK.containsKey(clid) && !Vars.AFKALLOWED.contains(c.getChannelId()) && !c.getPlatform().equalsIgnoreCase("ServerQuery")) {
                                            if (!Vars.PERMGROUPS.get(Vars.PERMGROUPASSIGNMENTS.get("afk")).isClientInGroup(uid)) {
                                                if ((c.getIdleTime() >= Vars.AFK_TIME) && isAfk) {
                                                    Vars.IN_AFK.put(clid, c.getChannelId());
                                                    API.moveClient(clid, Vars.AFK_CHANNEL_ID);
                                                    API.sendPrivateMessage(clid, Messages.get("you-were-moved-to-afk"));
                                                    System.out.println("[AFK Mover] " + c.getNickname() + "(" + clid + ") is afk.");
                                                }
                                            }
                                        } else if (Vars.IN_AFK.containsKey(clid)) {
                                            if ((c.getIdleTime() < Vars.AFK_TIME)) {
                                                if (!Vars.AFKMOVEBL.contains(Vars.IN_AFK.get(clid))) {
                                                    API.sendPrivateMessage(clid, Messages.get("you-were-moved-back-from-afk"));
                                                    API.moveClient(clid, Vars.IN_AFK.get(clid));
                                                }
                                                Vars.IN_AFK.remove(clid);
                                                System.out.println("[AFK Mover] " + c.getNickname() + "(" + clid + ") is back again.");
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    System.out.println("[Connection] Request Timeout.");
                }
            }
        }, 0, Vars.TIMER_TICK, TimeUnit.MILLISECONDS));
        System.out.println("[Internal] Service Running");
    }

    static void startBroadCast() {
        tasks.add(Vars.SERVICE.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (Vars.BROADCAST_ENABLED)
                    try {
                        Random r = new Random();
                        final int i = r.nextInt(Vars.BROADCASTS.size());
                        if (Vars.DEBUG == 2)
                            System.out.println("[Broadcast] Sending...");
                        API.getClients().onSuccess(new CommandFuture.SuccessListener<List<Client>>() {
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
                                        API.sendPrivateMessage(c.getId(), Vars.BROADCASTS.get(i));
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

            }
        }, 0, Vars.BROADCAST_INTERVAL, TimeUnit.SECONDS));
    }

    static void startVPNChecker() {
        tasks.add(Vars.SERVICE.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (Vars.VPNCHECKER_ENABLED) {
                    if (Vars.DEBUG > 1)
                        System.out.println("[VPN Checker] Checking server for VPNs.");
                    API.getClients().onSuccess(new CommandFuture.SuccessListener<List<Client>>() {
                        @Override
                        public void handleSuccess(List<Client> result) {
                            for (Client c : result) {
                                if (c.isServerQueryClient())
                                    return;
                                VPNChecker check = new VPNChecker(c);
                                if (check.isBlocked()) {
                                    System.out.println("[VPN Checker] " + c.getNickname() + " was kicked. Blacklisted IP: " + c.getIp());
                                    API.kickClientFromServer(Messages.get("you-are-using-vpn"), c.getId());
                                }
                            }
                        }
                    });
                }
            }

        }, 0, Vars.VPNCHECKER_INTERVAL, TimeUnit.SECONDS));
    }

    static void startChannelStats() {
        tasks.add(Vars.SERVICE.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    final Calendar calendar = Calendar.getInstance();
                    final Pattern groupPattern = Pattern.compile("<group>(.+?)</group>");
                    final Pattern timePattern = Pattern.compile("<time>(.+?)</time>");
                    final Pattern datePattern = Pattern.compile("<date>(.+?)</date>");
                    List<Client> clients = API.getClients().getUninterruptibly();
                    for (int channelId : Vars.CHANNELSTATS.keySet()) {
                        ChannelInfo channel = API.getChannelInfo(channelId).getUninterruptibly();
                        String channelName = Vars.CHANNELSTATS.get(channelId);
                        String description = "";

                        Matcher matcher = groupPattern.matcher(channelName);
                        if (matcher.find()) {
                            String group = matcher.group(1);
                            if (Vars.PERMGROUPS.containsKey(group)) {
                                PermissionGroup permissionGroup = Vars.PERMGROUPS.get(group);
                                int count = 0;
                                StringBuilder sb = new StringBuilder("[center][size=15]Group Info[/size][/center][hr] \n\n");
                                for (Client c : clients) {
                                    ClientInfo info = API.getClientInfo(c.getId()).getUninterruptibly();
                                    ChannelInfo cInfo = API.getChannelInfo(c.getChannelId()).getUninterruptibly();
                                    if (permissionGroup.isClientInGroup(c.getUniqueIdentifier())) {
                                        sb.append("[b][URL=client://").append(c.getId()).append("/").append(c.getUniqueIdentifier()).append("~").append(c.getNickname()).append("]").append(c.getNickname()).append("[/URL][/b]\n").append("  Channel: [b][url=channelID://").append(c.getChannelId()).append("]").append(cInfo.getName()).append("[/url][/b]\n").append("  Online Since: [b]").append(new DecimalFormat("#.##").format(info.getTimeConnected() / 1000 / 60)).append(" minutes[/b]\n");
                                        if (!info.getDescription().equalsIgnoreCase(""))
                                            sb.append("  Description: [b]").append(info.getDescription()).append("[/b]\n");
                                        count++;
                                    }
                                }
                                sb.append("\n[hr]");
                                channelName = channelName.replace("<group>" + group + "</group>", String.valueOf(count));
                                description = sb.toString();
                            }
                        }

                        matcher = timePattern.matcher(channelName);
                        if (matcher.find()) {
                            String timeFormat = matcher.group(1);
                            SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
                            channelName = channelName.replace("<time>" + timeFormat + "</time>", sdf.format(calendar.getTime()));
                        }

                        matcher = datePattern.matcher(channelName);
                        if (matcher.find()) {
                            String dateFormat = matcher.group(1);
                            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                            channelName = channelName.replace("<date>" + dateFormat + "</date>", sdf.format(calendar.getTime()));
                        }
                        if (channelName.length() > 40) {
                            System.err.println("[Channel Stats] Generated channel name of channel_" + channelId + " is too long (" + channelName.length() + "/40). Generated Name: " + channelName);
                            return;
                        }
                        Map<ChannelProperty, String> options = new HashMap<>();
                        if (!channel.getName().equals(channelName))
                            options.put(ChannelProperty.CHANNEL_NAME, channelName);
                        if (!channel.getDescription().equals(description) && !channel.getDescription().equals(""))
                            options.put(ChannelProperty.CHANNEL_DESCRIPTION, description);
                        API.editChannel(channelId, options);
                    }
                } catch (Exception ignored) {
                }
            }
        }, 0, 60, TimeUnit.SECONDS));
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
        for (ScheduledFuture<?> future : tasks) {
            future.cancel(true);
        }
        tasks.clear();
    }

}
