package ga.scrumplex.ml.sprum.sprummlbot;

import java.io.File;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import ga.scrumplex.ml.sprum.sprummlbot.stuff.ConfigException;
import ga.scrumplex.ml.sprum.sprummlbot.stuff.Language;

public class Configuration {
	
	public static void load(File f) throws Exception {
		Ini ini = new Ini(f);
		
		if(!ini.containsKey("Connection")){
			throw new ConfigException("Connection section is not there!");
		}
		if(!ini.containsKey("Login")){
			throw new ConfigException("Login section is not there!");
		}
		if(!ini.containsKey("Webinterface")){
			throw new ConfigException("Webinterface section is not there!");
		}
		if(!ini.containsKey("Appearance")){
			throw new ConfigException("Appearance section is not there!");
		}
		if(!ini.containsKey("Serverteam")){
			throw new ConfigException("Serverteam section is not there!");
		}
		if(!ini.containsKey("AFK Mover")){
			throw new ConfigException("AFK Mover section is not there!");
		}
		if(!ini.containsKey("Support Reminder")){
			throw new ConfigException("Support Reminder section is not there!");
		}
		if(!ini.containsKey("Commands")){
			throw new ConfigException("Commands section is not there!");
		}
		if(!ini.containsKey("Messages")){
			throw new ConfigException("Messages section is not there!");
		}
		if(!ini.containsKey("Misc")){
			throw new ConfigException("Misc section is not there!");
		}
		
		Section connection = ini.get("Connection");
		
		if(!connection.containsKey("ip") || !connection.containsKey("port")) {
			throw new ConfigException("Connection not defined carefully!");
		}
		Config.server = connection.get("ip");
		Config.port = connection.get("port", int.class);
		
		Section login = ini.get("Login");
		
		if(!login.containsKey("username") || !login.containsKey("password") || !login.containsKey("server-id")) {
			throw new ConfigException("Login not defined carefully!");
		}
		
		Config.login[0] = login.get("username");
		Config.login[1] = login.get("password");
		Config.vserver = login.get("server-id", int.class);

		Section webinterface = ini.get("Webinterface");
		
		if(!webinterface.containsKey("port")) {
			throw new ConfigException("Webinterface not defined carefully!");
		}
		
		Config.webport = webinterface.get("port", int.class);

		Section appearance = ini.get("Appearance");
		
		if(!appearance.containsKey("nickname")) {
			throw new ConfigException("Appearance not defined carefully!");
		}
		
		Config.botname = appearance.get("nickname");

		Section serverteam = ini.get("Serverteam");

		if(!serverteam.containsKey("uid")) {
			throw new ConfigException("Serverteam not defined carefully!");
		}
		
		String[] admins = serverteam.getAll("uid", String[].class);
		for(String uid : admins) {
			Config.admins.add(uid);
		}
		
		Section afkmover = ini.get("AFK Mover");
		
		if(!afkmover.containsKey("enabled") || !afkmover.containsKey("move-server-team") || !afkmover.containsKey("channelid") || !afkmover.containsKey("maxafktime") || !afkmover.containsKey("afk-allowed-channel-id")) {
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

		if(!supportreminder.containsKey("enabled") || !supportreminder.containsKey("channelid")) {
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
		
		if(!misc.containsKey("language") || !misc.containsKey("debug") || !misc.containsKey("check-tick") || !misc.containsKey("update-notification")) {
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
		
		if(!messages.containsKey("skype-id") || !messages.containsKey("website") || !messages.containsKey("youtube")) {
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
