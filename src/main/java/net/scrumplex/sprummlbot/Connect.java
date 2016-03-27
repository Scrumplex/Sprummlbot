package net.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.CommandFuture;
import com.github.theholywaffle.teamspeak3.api.exception.TS3ConnectionFailedException;
import com.github.theholywaffle.teamspeak3.api.reconnect.ReconnectingConnectionHandler;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerQueryInfo;
import net.scrumplex.sprummlbot.tools.Exceptions;

import java.util.List;
import java.util.logging.Level;

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
                    System.out.println("Initializing Sprummlbot...");
                } else {
                    System.out.println("Reinitializing Sprummlbot...");
                }
                if (Vars.FLOODRATE != TS3Query.FloodRate.UNLIMITED)
                    System.out.println("NOTE: Do not forget to add the Sprummlbot to the ip white-list of your ts3 server and enable the \"can-flood\" feature in the config file. Located under Misc -> can-flood.");

                Vars.QUERY = ts3Query;
                if (Vars.QUERY.getAsyncApi() == null)
                    return;
                Vars.API = Vars.QUERY.getAsyncApi();
                try {
                    Vars.SPRUMMLBOT_STATUS = State.RECONNECTING;
                    Vars.API.login(Vars.LOGIN[0], Vars.LOGIN[1]);

                    System.out.println("Selecting Server " + Vars.SERVER_ID);
                    Vars.API.selectVirtualServerById(Vars.SERVER_ID);

                    Vars.API.setNickname(Vars.NICK).get();

                    System.out.println("Changing ServerQuery Rights");
                    ServerOptimization.permissions();
                    if (Vars.CHANGE_FLOOD_SETTINGS)
                        ServerOptimization.changeFloodLimits();

                    if (Vars.DEBUG > 1)
                        System.out.println(Vars.API.whoAmI().toString());

                    Vars.API.whoAmI().onSuccess(new CommandFuture.SuccessListener<ServerQueryInfo>() {
                        @Override
                        public void handleSuccess(ServerQueryInfo serverQueryInfo) {
                            Vars.QID = serverQueryInfo.getId();
                        }
                    });

                    System.out.println("Starting Event Service...");
                    Vars.API.registerAllEvents();
                    Events.start();

                    Tasks.stopAll();
                    if (Vars.AFK_ENABLED || Vars.SUPPORT_ENABLED || Vars.ANTIREC_ENABLED || Vars.GROUPPROTECT_ENABLED) {
                        System.out.println("Starting Main Service...");
                        Tasks.startService();
                    }

                    if (Vars.BROADCAST_ENABLED) {
                        System.out.println("Starting Broadcaster Service...");
                        Tasks.startBroadCast();
                    }

                    if (Vars.VPNCHECKER_ENABLED) {
                        System.out.println("Starting VPN Checker Service...");
                        Tasks.startVPNChecker();
                    }

                    if (Vars.LOGGER_ENABLED) {
                        System.out.println("Starting Server Logger Service...");
                        ServerLogger.start();
                    }

                    if (Vars.CHANNELSTATS_ENABLED) {
                        System.out.println("Starting Channel Stats Service...");
                        Tasks.startChannelStats();
                    }

                    Vars.API.getClients().onSuccess(new CommandFuture.SuccessListener<List<Client>>() {
                        @Override
                        public void handleSuccess(List<Client> result) {
                            for (Client c : result) {
                                if (Vars.PERMGROUPS.get(Vars.PERMGROUPASSIGNMENTS.get("notify")).isClientInGroup(c.getUniqueIdentifier())) {
                                    Vars.API.sendPrivateMessage(c.getId(), "Sprummlbot connected!" + (Vars.UPDATE_AVAILABLE ? " An update is available! Please update!" : ""));

                                }
                            }
                        }
                    });
                    Vars.SPRUMMLBOT_STATUS = State.RUNNING;
                } catch (InterruptedException ignored) {
                    Exceptions.handle(ignored, "While Init");
                }
            }
        });

        System.out.println("Debug Mode: " + Vars.DEBUG);
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

        System.out.println("Connecting to " + Vars.SERVER + ":" + Vars.PORT_SQ + " with credentials: " + Vars.LOGIN[0]
                + ", ******");

        Vars.QUERY = new TS3Query(config);
        Vars.QUERY.connect();
    }
}
