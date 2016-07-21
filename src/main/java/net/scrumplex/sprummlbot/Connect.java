package net.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.CommandFuture;
import com.github.theholywaffle.teamspeak3.api.VirtualServerProperty;
import com.github.theholywaffle.teamspeak3.api.exception.TS3ConnectionFailedException;
import com.github.theholywaffle.teamspeak3.api.reconnect.ConnectionHandler;
import com.github.theholywaffle.teamspeak3.api.reconnect.ReconnectStrategy;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import net.scrumplex.sprummlbot.core.Clients;
import net.scrumplex.sprummlbot.plugins.events.EventManager;
import net.scrumplex.sprummlbot.tools.Exceptions;
import net.scrumplex.sprummlbot.wrapper.State;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

class Connect {

    static void init() throws TS3ConnectionFailedException {
        Vars.SPRUMMLBOT_STATUS = State.CONNECTING;
        final TS3Config config = new TS3Config();
        config.setHost(Vars.SERVER);
        config.setQueryPort(Vars.PORT_SQ);
        config.setFloodRate(Vars.FLOODRATE);
        config.setReconnectStrategy(ReconnectStrategy.exponentialBackoff());
        config.setConnectionHandler(new ConnectionHandler() {
            @Override
            public void onConnect(TS3Query ts3Query) {
                if (Vars.SPRUMMLBOT_STATUS == State.STOPPING) {
                    ts3Query.exit();
                    return;
                }
                final Sprummlbot sprummlbot = Sprummlbot.getSprummlbot();
                Vars.RECONNECT_TIMES++;
                if (Vars.RECONNECT_TIMES == 0) {
                    System.out.println("[Core] Initializing Sprummlbot...");
                } else {
                    System.out.println("[Core] Reinitializing Sprummlbot...");
                }
                if (Vars.FLOODRATE != TS3Query.FloodRate.UNLIMITED)
                    System.out.println("[Note] Do not forget adding the IP of your Sprummlbot to the query_whitelist.txt of your ts3 server and enabling the \"can-flood\" feature in the config file. It is Located under Misc -> can-flood.");

                Vars.QUERY = ts3Query;
                if (Vars.QUERY.getAsyncApi() == null)
                    return;
                sprummlbot.setTS3Api(ts3Query.getApi());
                sprummlbot.setTS3ApiAsync(ts3Query.getAsyncApi());
                final TS3Api api = sprummlbot.getSyncAPI();
                try {
                    Vars.SPRUMMLBOT_STATUS = State.RECONNECTING;
                    api.login(Vars.LOGIN[0], Vars.LOGIN[1]);

                    System.out.println("[Core] Selecting Server " + Vars.SERVER_ID);
                    api.selectVirtualServerById(Vars.SERVER_ID);

                    api.setNickname(Vars.NICK);

                    System.out.println("[Core] Changing ServerQuery Rights");
                    ServerOptimization.applyPermissions();
                    if (Vars.CHANGE_FLOOD_SETTINGS)
                        ServerOptimization.applyFloodRateSettings();

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
                    api.registerAllEvents();
                    Events.start();
                    Vars.clients = new Clients();
                    sprummlbot.setMainEventManager(new EventManager(null));
                    if (Vars.VPNCHECKER_ENABLED) {
                        System.out.println("[VPN Checker] Starting VPN Checker...");
                        Tasks.startVPNChecker();
                    }

                    if (Vars.DYNBANNER_ENABLED) {
                        Map<VirtualServerProperty, String> settings = new HashMap<>();
                        settings.put(VirtualServerProperty.VIRTUALSERVER_HOSTBANNER_GFX_URL,
                                "http://" + Vars.IP + ":9911/f/banner.png");
                        settings.put(VirtualServerProperty.VIRTUALSERVER_HOSTBANNER_GFX_INTERVAL, "60");
                        api.editServer(settings);
                    }

                    sprummlbot.getDefaultAPI().getClients().onSuccess(new CommandFuture.SuccessListener<List<Client>>() {
                        @Override
                        public void handleSuccess(List<Client> result) {
                            for (Client c : result) {
                                if (Vars.PERMGROUPS.get(Vars.PERMGROUPASSIGNMENTS.get("notify")).isClientInGroup(c.getUniqueIdentifier()))
                                    sprummlbot.getDefaultAPI().sendPrivateMessage(c.getId(), "Sprummlbot connected!" + (Vars.UPDATE_AVAILABLE ? " An update is available! Please update!" : ""));
                            }
                        }
                    });
                    Vars.SPRUMMLBOT_STATUS = State.RUNNING;
                    if (Vars.RECONNECT_TIMES > 0) {
                        sprummlbot.getModuleManager().stopAllModules();
                        sprummlbot.getModuleManager().startAllModules();
                    }
                } catch (Exception ignored) {
                    Exceptions.handle(ignored, "An error occurred while initializing the Sprummlbot!");
                }
            }

            @Override
            public void onDisconnect(TS3Query ts3Query) {
                System.out.println("[Core] Lost connection to TeamSpeak 3 Server! Reconnecting...");
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

        System.out.println("[Core] Connecting to " + Vars.SERVER + ":" + Vars.PORT_SQ + "...");

        Vars.QUERY = new TS3Query(config);
        Vars.QUERY.connect();
    }
}
