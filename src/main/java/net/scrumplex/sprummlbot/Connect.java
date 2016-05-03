package net.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.CommandFuture;
import com.github.theholywaffle.teamspeak3.api.VirtualServerProperty;
import com.github.theholywaffle.teamspeak3.api.exception.TS3ConnectionFailedException;
import com.github.theholywaffle.teamspeak3.api.reconnect.ReconnectingConnectionHandler;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerQueryInfo;
import net.scrumplex.sprummlbot.tools.Exceptions;
import net.scrumplex.sprummlbot.wrapper.State;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static net.scrumplex.sprummlbot.Vars.API;

class Connect {

    static void init() throws TS3ConnectionFailedException {
        Vars.SPRUMMLBOT_STATUS = State.CONNECTING;
        final TS3Config config = new TS3Config();
        config.setHost(Vars.SERVER);
        config.setQueryPort(Vars.PORT_SQ);
        config.setFloodRate(Vars.FLOODRATE);
        config.setConnectionHandler(new ReconnectingConnectionHandler() {
            @Override
            public void setUpQuery(TS3Query ts3Query) {
                if (Vars.SPRUMMLBOT_STATUS == State.STOPPING) {
                    ts3Query.exit();
                    return;
                }
                Vars.RECONNECT_TIMES++;
                if (Vars.RECONNECT_TIMES == 0) {
                    System.out.println("[Internal] Initializing Sprummlbot...");
                } else {
                    System.out.println("[Internal] Reinitializing Sprummlbot...");
                }
                if (Vars.FLOODRATE != TS3Query.FloodRate.UNLIMITED)
                    System.out.println("[Connection] NOTE: Do not forget adding the IP of your Sprummlbot to the query_whitelist.txt of your ts3 server and enabling the \"can-flood\" feature in the config file. It is Located under Misc -> can-flood.");

                Vars.QUERY = ts3Query;
                if (Vars.QUERY.getAsyncApi() == null)
                    return;
                API = Vars.QUERY.getAsyncApi();
                try {
                    Vars.SPRUMMLBOT_STATUS = State.RECONNECTING;
                    API.login(Vars.LOGIN[0], Vars.LOGIN[1]);

                    System.out.println("[Connection] Selecting Server " + Vars.SERVER_ID);
                    API.selectVirtualServerById(Vars.SERVER_ID);

                    API.setNickname(Vars.NICK).get();

                    System.out.println("[Connection] Changing ServerQuery Rights");
                    ServerOptimization.permissions();
                    if (Vars.CHANGE_FLOOD_SETTINGS)
                        ServerOptimization.changeFloodLimits();

                    if (Vars.DYNBANNER_ENABLED) {
                        try {
                            System.out.println("[Dynamic Banner] Initializing Dynamic Banner...");
                            if (!Vars.DYNBANNER_FILE.exists())
                                Exceptions.handle(new FileNotFoundException("Banner file doesnt exist"),
                                        "Banner File doesn't exist", true);
                            Startup.banner = new DynamicBanner(Vars.DYNBANNER_FILE, Vars.DYNBANNER_COLOR,
                                    Vars.DYNBANNER_FONT);
                        } catch (IOException e) {
                            Exceptions.handle(e, "Error while initializing Dynamic Banner");
                        }
                    }

                    System.out.println("[Internal] Starting event service...");
                    API.registerAllEvents();
                    Events.start();

                    Tasks.stopAll();

                    if (Vars.AFK_ENABLED || Vars.SUPPORT_ENABLED || Vars.ANTIREC_ENABLED || Vars.GROUPPROTECT_ENABLED || Vars.DYNBANNER_ENABLED) {
                        System.out.println("[Internal] Starting main service...");
                        Tasks.startService();
                    }

                    if (Vars.BROADCAST_ENABLED) {
                        System.out.println("[Internal] Starting broadcaster service...");
                        Tasks.startBroadCast();
                    }

                    if (Vars.VPNCHECKER_ENABLED) {
                        System.out.println("[Internal] Starting VPN checker service...");
                        Tasks.startVPNChecker();
                    }

                    if (Vars.LOGGER_ENABLED) {
                        System.out.println("[Internal] Starting server logger service...");
                        ServerLogger.start();
                    }

                    if (Vars.CHANNELSTATS_ENABLED) {
                        System.out.println("[Internal] Starting channel stats service...");
                        Tasks.startChannelStats();
                    }

                    if(Vars.DYNBANNER_ENABLED) {
                        Map<VirtualServerProperty, String> settings = new HashMap<>();
                        settings.put(VirtualServerProperty.VIRTUALSERVER_HOSTBANNER_GFX_URL,
                                "http://" + Vars.IP + ":9911/f/banner.png");
                        settings.put(VirtualServerProperty.VIRTUALSERVER_HOSTBANNER_GFX_INTERVAL, "60");
                        API.editServer(settings);
                    }

                    API.getClients().onSuccess(new CommandFuture.SuccessListener<List<Client>>() {
                        @Override
                        public void handleSuccess(List<Client> result) {
                            for (Client c : result) {
                                if (Vars.PERMGROUPS.get(Vars.PERMGROUPASSIGNMENTS.get("notify")).isClientInGroup(c.getUniqueIdentifier()))
                                    API.sendPrivateMessage(c.getId(), "Sprummlbot connected!" + (Vars.UPDATE_AVAILABLE ? " An update is available! Please update!" : ""));
                            }
                        }
                    });
                    Vars.SPRUMMLBOT_STATUS = State.RUNNING;
                } catch (InterruptedException ignored) {
                    Exceptions.handle(ignored, "While Init");
                }
            }
        });

        System.out.println("[Internal] Debug Mode: " + Vars.DEBUG);
        switch (Vars.DEBUG) {
            case 1:
                config.setDebugLevel(Level.WARNING);
                break;

            case 2:
                config.setDebugLevel(Level.ALL);
                break;

            default:
                config.setDebugLevel(Level.OFF);
                break;
        }

        System.out.println("[Connection] Connecting to " + Vars.SERVER + ":" + Vars.PORT_SQ + "...");

        Vars.QUERY = new TS3Query(config);
        Vars.QUERY.connect();
    }
}
