package ga.scrumplex.ml.sprum.sprummlbot;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;

import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.TS3Query.FloodRate;

public class Startup extends Config{	

	public static void start() throws UnknownHostException, IOException {
		final TS3Config config = new TS3Config();
		config.setHost(server);
		config.setQueryPort(port);
		Logger.out("Debug Mode: " + Config.debug);
		switch(Config.debug) {
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
		config.setLoginCredentials(login[0], login[1]);
	
		Logger.out("Connecting to " + server + ":" + port + " with credentials: " + login[0] + ", " + login[1] + "...");
		query = new TS3Query(config);
		query.connect();
		Logger.out("Selecting Server " + vserver);
		api = query.getApi();
		api.selectVirtualServerById(vserver);
		api.setNickname(botname);
		
		Logger.out(api.whoAmI().toString());
		
		qID = api.whoAmI().getId();
		if(afk) {
			Logger.out("Starting AFK process...");
		}
		if(supports) {
			Logger.out("Starting Support process...");
		}
		if(afk || supports) {
			new Register(Config.timertick, api);
		}

		Logger.out("Events are being registered...");
		new Events();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    @Override
		    public void run() {
		        System.out.println("Sprummlbot is shutting down!");
			    Config.api.sendServerMessage("Sprummlbot is shutting down!");
		    }
		});
}
}
