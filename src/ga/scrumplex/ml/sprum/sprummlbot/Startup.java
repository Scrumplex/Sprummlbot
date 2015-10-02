package ga.scrumplex.ml.sprum.sprummlbot;

import java.util.logging.Level;

import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.TS3Query.FloodRate;
import com.github.theholywaffle.teamspeak3.api.exception.TS3ConnectionFailedException;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

public class Startup extends Config{	

	public static void start() {
		final TS3Config config = new TS3Config();
		config.setHost(SERVER);
		config.setQueryPort(PORT_SQ);
		Logger.out("Debug Mode: " + Config.DEBUG);
		switch(Config.DEBUG) {
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
		config.setLoginCredentials(LOGIN[0], LOGIN[1]);
	
		Logger.out("Connecting to " + SERVER + ":" + PORT_SQ + " with credentials: " + LOGIN[0] + ", ******");
		QUERY = new TS3Query(config);
		try {
			QUERY.connect();
		} catch(TS3ConnectionFailedException e) {
			throw e;
		}
		Logger.out("Selecting Server " + SERVERID);
		API = QUERY.getApi();
		API.selectVirtualServerById(SERVERID);
		API.setNickname(NICK);
		
		if(Config.DEBUG > 1) Logger.out(API.whoAmI().toString());
		
		QID = API.whoAmI().getId();
		if(AFK_ENABLED) Logger.out("Starting AFK process...");
		if(SUPPORT_ENABLED) Logger.out("Starting Support process...");
		if(AFK_ENABLED || SUPPORT_ENABLED) new Register();

		Logger.out("Events are being registered...");
		Events.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    @Override
		    public void run() {
		        System.out.println("Sprummlbot is shutting down!");
				for(Client c : API.getClients()) {
					if(TEAM.contains(c.getUniqueIdentifier())) {
						API.sendPrivateMessage(c.getId(), "Sprummlbot is shutting down!");
					}
				}
				QUERY.exit();
		    }
		});
	}
}
