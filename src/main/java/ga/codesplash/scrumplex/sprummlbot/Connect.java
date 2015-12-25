package ga.codesplash.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.CommandFuture;
import com.github.theholywaffle.teamspeak3.api.exception.TS3ConnectionFailedException;
import com.github.theholywaffle.teamspeak3.api.reconnect.ReconnectingConnectionHandler;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerQueryInfo;

import java.util.List;
import java.util.logging.Level;

class Connect {

    static void init() throws TS3ConnectionFailedException {
        final TS3Config config = new TS3Config();
        config.setHost(Vars.SERVER);
        config.setQueryPort(Vars.PORT_SQ);
        config.setFloodRate(Vars.FLOODRATE);

        config.setConnectionHandler(new ReconnectingConnectionHandler() {

            @Override
            public void setUpQuery(TS3Query ts3Query) {
                Vars.RECONNECT_TIMES++;
                if(Vars.RECONNECT_TIMES == 0) {
                    System.out.println("Initializing Sprummlbot...");
                } else {
                    System.out.println("Reinitializing Sprummlbot...");
                }
                Vars.QUERY = ts3Query;
                if(Vars.QUERY.getAsyncApi() == null)
                    return;
                Vars.API = Vars.QUERY.getAsyncApi();
                try {
                    connect();
                } catch (InterruptedException ignored) {
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

        Vars.API = Vars.QUERY.getAsyncApi();
        Events.start();
    }

    private static void connect() throws InterruptedException {
        Vars.API.login(Vars.LOGIN[0], Vars.LOGIN[1]);

        System.out.println("Selecting Server " + Vars.SERVER_ID);
        Vars.API.selectVirtualServerById(Vars.SERVER_ID);

        Vars.API.setNickname(Vars.NICK).get();

        System.out.println("Changing ServerQuery Rights");
        ServerOptimization.permissions();
        ServerOptimization.changeFloodLimits();

        if (Vars.DEBUG > 1)
            System.out.println(Vars.API.whoAmI().toString());

        Vars.API.whoAmI().onSuccess(new CommandFuture.SuccessListener<ServerQueryInfo>() {
            @Override
            public void handleSuccess(ServerQueryInfo serverQueryInfo) {
                Vars.QID = serverQueryInfo.getId();
            }
        });

        Tasks.stopAll();
        if (Vars.AFK_ENABLED)
            System.out.println("Starting AFK Mover...");
        if (Vars.SUPPORT_ENABLED)
            System.out.println("Starting Support Notifier...");
        if (Vars.ANTIREC_ENABLED)
            System.out.println("Starting Anti Record Handler...");
        if (Vars.GROUPPROTECT_ENABLED)
            System.out.println("Starting Group Protector...");
        if (Vars.AFK_ENABLED || Vars.SUPPORT_ENABLED || Vars.ANTIREC_ENABLED || Vars.GROUPPROTECT_ENABLED)
            Tasks.startService();

        if (Vars.BROADCAST_ENABLED) {
            System.out.println("Starting Broadcaster");
            Tasks.startBroadCast();
        }

        if (Vars.VPNCHECKER_ENABLED) {
            System.out.println("Starting VPN Checker...");
            Tasks.startVPNChecker();
        }

        System.out.println("Events are being registered...");
        Vars.API.registerAllEvents();

        Vars.API.getClients().onSuccess(new CommandFuture.SuccessListener<List<Client>>() {
            @Override
            public void handleSuccess(List<Client> result) {
                for (Client c : result) {
                    if (Vars.NOTIFY.contains(c.getUniqueIdentifier())) {
                        Vars.API.sendPrivateMessage(c.getId(), "Sprummlbot connected!");
                    }
                }
            }
        });
    }
}
