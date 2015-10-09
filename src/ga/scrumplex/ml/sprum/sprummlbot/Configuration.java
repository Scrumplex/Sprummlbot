package ga.scrumplex.ml.sprum.sprummlbot;

import java.io.File;
import java.util.List;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import ga.scrumplex.ml.sprum.sprummlbot.bridge.TCPServer;
import ga.scrumplex.ml.sprum.sprummlbot.stuff.ConfigException;
import ga.scrumplex.ml.sprum.sprummlbot.stuff.Language;

public class Configuration {

	public static void load(File f) throws Exception {
		Ini ini = new Ini(f);

		if (!ini.containsKey("Connection")) {
			throw new ConfigException("Connection section was not defined!");
		}
		if (!ini.containsKey("Login")) {
			throw new ConfigException("Login section was not defined!");
		}
		if (!ini.containsKey("Webinterface")) {
			throw new ConfigException("Webinterface section was not defined!");
		}
		if (!ini.containsKey("Appearance")) {
			throw new ConfigException("Appearance section was not defined!");
		}
		if (!ini.containsKey("Serverteam")) {
			throw new ConfigException("Serverteam section was not defined!");
		}
		if (!ini.containsKey("AFK Mover")) {
			throw new ConfigException("AFK Mover section was not defined!");
		}
		if (!ini.containsKey("Support Reminder")) {
			throw new ConfigException("Support Reminder section was not defined!");
		}
		if (!ini.containsKey("Anti Recording")) {
			throw new ConfigException("Bad Names section was not defined!");
		}
		if (!ini.containsKey("TCP Bridge API")) {
			throw new ConfigException("TCP Bridge API section was not defined!");
		}
		if (!ini.containsKey("Commands")) {
			throw new ConfigException("Commands section was not defined!");
		}
		if (!ini.containsKey("Messages")) {
			throw new ConfigException("Messages section was not defined!");
		}
		if (!ini.containsKey("Misc")) {
			throw new ConfigException("Misc section was not defined!");
		}

		Section connection = ini.get("Connection");

		if (!connection.containsKey("ip") || !connection.containsKey("port")) {
			throw new ConfigException("Connection not defined carefully!");
		}
		Config.SERVER = connection.get("ip");
		Config.PORT_SQ = connection.get("port", int.class);

		Section login = ini.get("Login");

		if (!login.containsKey("username") || !login.containsKey("password") || !login.containsKey("server-id")) {
			throw new ConfigException("Login not defined carefully!");
		}

		Config.LOGIN[0] = login.get("username");
		Config.LOGIN[1] = login.get("password");
		Config.SERVERID = login.get("server-id", int.class);

		Section webinterface = ini.get("Webinterface");

		if (!webinterface.containsKey("port")) {
			throw new ConfigException("Webinterface not defined carefully!");
		}

		Config.PORT_WI = webinterface.get("port", int.class);

		Section appearance = ini.get("Appearance");

		if (!appearance.containsKey("nickname")) {
			throw new ConfigException("Appearance not defined carefully!");
		}

		Config.NICK = appearance.get("nickname");

		Section serverteam = ini.get("Serverteam");

		if (!serverteam.containsKey("uid")) {
			throw new ConfigException("Serverteam not defined carefully!");
		}

		String[] admins = serverteam.getAll("uid", String[].class);
		for (String uid : admins) {
			Config.TEAM.add(uid);
		}

		Section afkmover = ini.get("AFK Mover");

		if (!afkmover.containsKey("enabled") || !afkmover.containsKey("move-server-team")
				|| !afkmover.containsKey("channelid") || !afkmover.containsKey("maxafktime")
				|| !afkmover.containsKey("afk-allowed-channel-id")) {
			throw new ConfigException("AFK Mover not defined carefully!");
		}

		Config.AFK_ENABLED = afkmover.get("enabled", boolean.class);
		Config.AFK_MOVE_TEAM = afkmover.get("move-server-team", boolean.class);
		Config.AFKCHANNELID = afkmover.get("channelid", int.class);
		Config.AFKTIME = afkmover.get("maxafktime", int.class) * 1000;
		int[] dontmove = serverteam.getAll("afk-allowed-channel-id", int[].class);
		for (int id : dontmove) {
			Config.AFKALLOWED.add(id);
		}

		Section supportreminder = ini.get("Support Reminder");

		if (!supportreminder.containsKey("enabled") || !supportreminder.containsKey("channelid")) {
			throw new ConfigException("Support Reminder not defined carefully!");
		}

		Config.SUPPORT_ENABLED = supportreminder.get("enabled", boolean.class);
		Config.SUPPORTCHANNELID = supportreminder.get("channelid", int.class);

		Section antirec = ini.get("Support Reminder");

		if (!antirec.containsKey("enabled") || !antirec.containsKey("channelid")) {
			throw new ConfigException("Anti Recording not defined carefully!");
		}

		Config.ANTIREC_ENABLED = antirec.get("enabled", boolean.class);
		Config.ANTIREC_IGNORE_TEAM = antirec.get("ignore-team", boolean.class);

		Section commands = ini.get("Commands");
		if (commands.containsKey("disabled")) {
			Commands.setup(commands.getAll("disabled", String[].class));
		} else {
			Commands.setup();
		}

		Section api = ini.get("TCP Bridge API");
		if (!api.containsKey("enabled") || !api.containsKey("port") || !api.containsKey("whitelisted-ip")) {
			throw new ConfigException("TCP Bridge API not defined carefully!");
		}
		
		Config.BRIDGE_ENABLED = api.get("enabled", boolean.class);
		Config.PORT_BRIDGE = api.get("port", int.class);
		List<String> ips = api.getAll("whitelisted-ip");
		for (String ip : ips) {
			TCPServer.addToWhitelist(ip);
		}
		
		Section misc = ini.get("Misc");

		if (!misc.containsKey("language") || !misc.containsKey("debug") || !misc.containsKey("check-tick")
				|| !misc.containsKey("update-notification")) {
			throw new ConfigException("Misc not defined carefully!");
		}

		if (Language.fromID(misc.get("language")) != null) {
			Messages.setupLanguage(Language.fromID(misc.get("language")));
		} else {
			Logger.warn("You defined a not supported language in config! Setting to EN!");
			Messages.setupLanguage(Language.EN);
		}

		Config.UPDATER_ENABLED = misc.get("update-notification", boolean.class);

		Config.TIMERTICK = misc.get("check-tick", int.class);

		Config.DEBUG = misc.get("debug", int.class);

		Section messages = ini.get("Messages");

		if (!messages.containsKey("skype-id") || !messages.containsKey("website") || !messages.containsKey("youtube")) {
			throw new ConfigException("Messages not defined carefully!");
		}

		Messages.add("skype", messages.get("skype-id"));
		Messages.add("website", messages.get("website"));
		Messages.add("youtube", messages.get("youtube"));

		Config.AFKALLOWED.add(Config.AFKCHANNELID);
		Config.AFKALLOWED.add(Config.SUPPORTCHANNELID);

		Logger.out("Config loaded!");
		if (Config.DEBUG == 2) {
			for (String str : ini.keySet()) {
				for (String out : ini.get(str).keySet()) {
					Logger.out("[DEBUG] [CONF] " + str + "." + out + ": " + ini.get(str).get(out));
				}
			}
		}
	}
}
