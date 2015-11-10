package ga.codesplash.scrumplex.sprummlbot;

import java.util.logging.Level;

import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.TS3Query.FloodRate;
import com.github.theholywaffle.teamspeak3.api.exception.TS3ConnectionFailedException;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import ga.codesplash.scrumplex.sprummlbot.stuff.ServerOptimization;

public class Startup {

	public static void start() throws TS3ConnectionFailedException {
		Tasks.init();
		final TS3Config config = new TS3Config();
		config.setHost(Vars.SERVER);
		config.setQueryPort(Vars.PORT_SQ);
		Logger.out("Debug Mode: " + Vars.DEBUG);
		switch (Vars.DEBUG) {
		case 0:
			config.setDebugLevel(Level.OFF);
			break;

		case 1:
			config.setDebugLevel(Level.WARNING);
			break;

		case 2:
			config.setDebugLevel(Level.ALL);
			break;
		}
		config.setFloodRate(FloodRate.UNLIMITED);
		config.setLoginCredentials(Vars.LOGIN[0], Vars.LOGIN[1]);

		Logger.out("Connecting to " + Vars.SERVER + ":" + Vars.PORT_SQ + " with credentials: " + Vars.LOGIN[0]
				+ ", ******");
		Vars.QUERY = new TS3Query(config);
		try {
			Vars.QUERY.connect();
		} catch (TS3ConnectionFailedException e) {
			throw e;
		}
		Logger.out("Selecting Server " + Vars.SERVERID);
		Vars.API = Vars.QUERY.getApi();
		Vars.API.selectVirtualServerById(Vars.SERVERID);
		Vars.API.setNickname(Vars.NICK);
		Logger.out("Changing ServerQuery Rights");
		ServerOptimization.permissions();
		if (Vars.DEBUG > 1)
			Logger.out(Vars.API.whoAmI().toString());

		Vars.QID = Vars.API.whoAmI().getId();
		if (Vars.AFK_ENABLED)
			Logger.out("Starting AFK process...");
		if (Vars.SUPPORT_ENABLED)
			Logger.out("Starting Support process...");
		if (Vars.ANTIREC_ENABLED)
			Logger.out("Starting Anti Record process...");
		if (Vars.GROUPPROTECT_ENABLED)
			Logger.out("Starting Groupprotect process...");
		if (Vars.AFK_ENABLED || Vars.SUPPORT_ENABLED || Vars.ANTIREC_ENABLED || Vars.GROUPPROTECT_ENABLED)
			Tasks.startService();

		if (Vars.BROADCAST_ENABLED) {
			Logger.out("Starting Broadcast Service");
			Tasks.startBroadCast();
		}

		Logger.out("Events are being registered...");
		Events.start();

		Logger.out("Starting Keep Alive Process");
		Tasks.startKeepAlive();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Sprummlbot is shutting down!");
				for (Client c : Vars.API.getClients()) {
					if (Vars.NOTIFY.contains(c.getUniqueIdentifier())) {
						Vars.API.sendPrivateMessage(c.getId(), "Sprummlbot is shutting down!");
					}
				}
				Vars.QUERY.exit();
			}
		});
	}
}
