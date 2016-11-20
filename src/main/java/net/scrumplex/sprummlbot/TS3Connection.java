package net.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.CommandFuture;
import com.github.theholywaffle.teamspeak3.api.VirtualServerProperty;
import com.github.theholywaffle.teamspeak3.api.reconnect.ConnectionHandler;
import com.github.theholywaffle.teamspeak3.api.reconnect.ReconnectStrategy;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import net.scrumplex.sprummlbot.core.Clients;
import net.scrumplex.sprummlbot.core.SprummlbotInitializationException;
import net.scrumplex.sprummlbot.module.ModuleLoadException;
import net.scrumplex.sprummlbot.plugins.events.EventManager;
import net.scrumplex.sprummlbot.tools.Exceptions;
import net.scrumplex.sprummlbot.wrapper.PermissionGroup;
import net.scrumplex.sprummlbot.wrapper.State;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

class TS3Connection {

    private final TS3Config config;
    private final String username;
    private final String password;
    private String nickname;
    private final int serverId;
    private TS3Query query;

    TS3Connection(TS3Config config, String username, String password, String nickname, int serverId) {
        this.config = config;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.serverId = serverId;
    }

    void initialize() {
        this.config.setConnectionHandler(new SprummlbotConnectionHandler());
        this.config.setReconnectStrategy(ReconnectStrategy.exponentialBackoff());

        // Pre-connect initialization
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

        if (Vars.DYNBANNER_ENABLED) {
            try {
                System.out.println("[Dynamic Banner] Initializing Dynamic Banner...");
                if (!Vars.DYNBANNER_FILE.exists())
                    Exceptions.handle(new FileNotFoundException("Banner file doesnt exist"),
                            "Banner File doesn't exist", true);
                Vars.DYNBANNER = new DynamicBanner(Vars.DYNBANNER_FILE, Vars.DYNBANNER_COLOR,
                        Vars.DYNBANNER_FONT);
            } catch (IOException e) {
                Exceptions.handle(e, "Error while initializing Dynamic Banner");
            }
        }

        query = new TS3Query(config);
        query.connect();

        //Post connect initialization
        Events.start();
    }

    private void connect(TS3Query query) throws SprummlbotInitializationException, ModuleLoadException {
        final Sprummlbot sprummlbot = Sprummlbot.getSprummlbot();

        sprummlbot.setTS3Api(query.getApi());
        sprummlbot.setTS3ApiAsync(query.getAsyncApi());

        final TS3Api api = sprummlbot.getSyncAPI();

        if (!api.login(username, password))
            throw new SprummlbotInitializationException("Authentication failed! Username or password wrong.");

        System.out.println("[Core] Selecting virtual server with ID " + serverId + "...");
        if (!api.selectVirtualServerById(serverId))
            throw new SprummlbotInitializationException("Unable to select server with ID " + serverId + "!");

        api.setNickname(nickname);
        api.registerAllEvents();

        sprummlbot.setClientManager(new Clients());
        sprummlbot.setMainEventManager(new EventManager(null));

        if (Vars.VPNCHECKER_ENABLED) {
            System.out.println("[VPN Checker] Enabling VPN Checker...");
            Tasks.startVPNChecker();
        }
        Tasks.startInternalRunner();


        if (Vars.DYNBANNER_ENABLED) {
            Map<VirtualServerProperty, String> settings = new HashMap<>();
            settings.put(VirtualServerProperty.VIRTUALSERVER_HOSTBANNER_GFX_URL,
                    "http://" + Vars.IP + ":9911/f/banner.png");
            settings.put(VirtualServerProperty.VIRTUALSERVER_HOSTBANNER_GFX_INTERVAL, "60");
            api.editServer(settings);
            Tasks.startDynamicBanner();
        }

        sprummlbot.getDefaultAPI().getClients().onSuccess(new CommandFuture.SuccessListener<List<Client>>() {
            @Override
            public void handleSuccess(List<Client> result) {
                for (Client c : result) {
                    if (PermissionGroup.getPermissionGroupForField("notify").isClientInGroup(c.getUniqueIdentifier()))
                        sprummlbot.getDefaultAPI().sendPrivateMessage(c.getId(), "Sprummlbot connected!" + (Vars.UPDATE_AVAILABLE ? " An update is available! Please update!" : ""));
                }
            }
        });

        sprummlbot.getModuleManager().startAllModules();

        sprummlbot.setSprummlbotState(State.RUNNING);
    }

    private void cleanup() {
        final Sprummlbot sprummlbot = Sprummlbot.getSprummlbot();
        System.out.println("Stopping running tasks...");
        Tasks.stopAll();

        sprummlbot.getModuleManager().stopAllModules();
    }

    public TS3Query getQuery() {
        return query;
    }


    private class SprummlbotConnectionHandler implements ConnectionHandler {
        @Override
        public void onConnect(TS3Query ts3Query) {
            Sprummlbot sprummlbot = Sprummlbot.getSprummlbot();
            if (sprummlbot.getSprummlbotState() == State.STOPPING) {
                ts3Query.exit();
                return;
            }
            System.out.println("[Core] Connected to TeamSpeak 3 Server!");
            System.out.println("[Core] Initializing Sprummlbot...");
            sprummlbot.setSprummlbotState(State.CONNECTING);
            query = ts3Query;
            try {
                connect(ts3Query);
            } catch (Exception ex) {
                Exceptions.handle(ex, "There was an error while initializing the Sprummlbot.");
            }
            sprummlbot.setSprummlbotState(State.RUNNING);
        }

        @Override
        public void onDisconnect(TS3Query ts3Query) {
            Sprummlbot sprummlbot = Sprummlbot.getSprummlbot();
            sprummlbot.setSprummlbotState(State.DISCONNECTED);
            System.out.println("[Core] Lost connection to server!");
            cleanup();
        }
    }
}
