package ga.scrumplex.ml.sprum.sprummlbot;

import java.io.File;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import ga.scrumplex.ml.sprum.sprummlbot.stuff.ConfigException;
import ga.scrumplex.ml.sprum.sprummlbot.stuff.Language;

public class Configuration {
	
	public static void load(File f) throws Exception {
		Ini ini = new Ini(f);
		
		Section connection = ini.get("Connection");
		
		if(connection.containsKey("ip") == false || connection.containsKey("port") == false) {
			throw new ConfigException("Connection not defined carefully!");
		}
		Config.server = connection.get("ip");
		Config.port = connection.get("port", int.class);
		
		Section login = ini.get("Login");
		
		if(login.containsKey("username") == false || login.containsKey("password") == false) {
			throw new ConfigException("Login not defined carefully!");
		}
		
		Config.login[0] = login.get("username");
		Config.login[1] = login.get("password");

		Section webinterface = ini.get("Webinterface");
		
		if(webinterface.containsKey("port") == false) {
			throw new ConfigException("Webinterface not defined carefully!");
		}
		
		Config.webport = webinterface.get("port", int.class);

		Section appearance = ini.get("Appearance");
		
		if(appearance.containsKey("nickname") == false) {
			throw new ConfigException("Appearance not defined carefully!");
		}
		
		Config.botname = appearance.get("nickname");

		Section serverteam = ini.get("Serverteam");

		if(serverteam.containsKey("uid") == false) {
			throw new ConfigException("Serverteam not defined carefully!");
		}
		
		String[] admins = serverteam.getAll("uid", String[].class);
		for(String uid : admins) {
			Config.admins.add(uid);
		}
		
		Section afkmover = ini.get("AFK Mover");
		
		if(afkmover.containsKey("enabled") == false || afkmover.containsKey("move-server-team") == false || afkmover.containsKey("channelid") == false || afkmover.containsKey("maxafktime") == false || afkmover.containsKey("afk-allowed-channel-id") == false) {
			throw new ConfigException("AFK Mover not defined carefully!");
		}
		
		Config.afk = afkmover.get("enabled", boolean.class);
		Config.moveadmins = afkmover.get("move-server-team", boolean.class);
		Config.afkchannelid = afkmover.get("channelid", int.class);
		Config.afkidle = afkmover.get("maxafktime", int.class) * 1000;
		int[] dontmove = serverteam.getAll("afk-allowed-channel-id", int[].class);
		for(int id : dontmove) {
			Config.deniedchannels.add(id);
		}
		
		Section supportreminder = ini.get("Support Reminder");

		if(supportreminder.containsKey("enabled") == false || supportreminder.containsKey("channelid") == false) {
			throw new ConfigException("Support Reminder not defined carefully!");
		}
		
		Config.supports = supportreminder.get("enabled", boolean.class);
		Config.supportchannelid = supportreminder.get("channelid", int.class);
		
		Section commands = ini.get("Commands");
		if(commands.containsKey("disabled")) {
			Commands.setup(commands.getAll("disabled", String[].class));
		} else {
			Commands.setup();
		}
		
		Section misc = ini.get("Misc");
		
		if(misc.containsKey("language") == false || misc.containsKey("debug") == false || misc.containsKey("check-tick") == false || misc.containsKey("update-notification") == false) {
			throw new ConfigException("Misc not defined carefully!");
		}
		
	    if(Language.fromID(misc.get("language")) != null) {
			Messages.setupLanguage(Language.fromID(misc.get("language")));
	    } else {
	    	Logger.warn("You defined a not supported language in config! Setting to EN!");
			Messages.setupLanguage(Language.EN);
	    }
	    
	    Config.updater = misc.get("update-notification", boolean.class);
	    
	    Config.timertick = misc.get("check-tick", int.class);
	    
		Config.debug  = misc.get("debug", int.class);
		
		Section messages = ini.get("Messages");
		
		if(messages.containsKey("skype-id") == false || messages.containsKey("website") == false || messages.containsKey("youtube") == false) {
			throw new ConfigException("Messages not defined carefully!");
		}
		
		Messages.add("skype", messages.get("skype-id"));
		Messages.add("website", messages.get("website"));
		Messages.add("youtube", messages.get("youtube"));
		
		Config.deniedchannels.add(Config.afkchannelid);
		Config.deniedchannels.add(Config.supportchannelid);
		
	    Logger.out("Config loaded!");
	}
}
