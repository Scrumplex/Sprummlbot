package ga.scrumplex.ml.sprum.sprummlbot;

import java.io.File;
import java.util.HashMap;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import ga.scrumplex.ml.sprum.sprummlbot.stuff.ConfigException;
import ga.scrumplex.ml.sprum.sprummlbot.stuff.Language;

public class Messages {

	public static Language lang = null;

	public static void setupLanguage(Language language) throws Exception {
		Logger.out("Sprummlbot language is: " + language.getID());
		lang = language;
		File f = new File("messages-" + language.getID() + ".ini");
		if (f.exists() == false) {
			throw new ConfigException("Language File(s) doesn't exist!");
		}
		Ini ini = new Ini(f);
		Section global = ini.get("Global");
		HashMap<String, String> map = new HashMap<>();
		for (String key : global.keySet()) {
			map.put(key, global.get(key));
		}

		if (map.containsKey("you-were-moved-to-afk") == false || map.containsKey("you-mustnt-record-here") == false
				|| map.containsKey("you-were-moved-back-from-afk") == false
				|| map.containsKey("you-joined-support-channel") == false
				|| map.containsKey("you-are-not-longer-in-support-queue") == false
				|| map.containsKey("someone-is-in-support") == false || map.containsKey("unknown-command") == false) {
			throw new ConfigException("Language File is not defined carefully!");
		}

		msg = map;

		if (lang == Language.DE) {
			msg.put("help-dialog", "Sprummlbot v" + Config.VERSION + ". Programmiert von " + Config.AUTHOR + ".");
			msg.put("commandslist", "Verfügbare Kommandos: [B]");
			msg.put("your-ip", "Deine öffentliche IP ist: [B]");
			msg.put("webinterface-disalbed", "Das Webinterface ist nicht aktiviert");
			msg.put("webinterface-your-user", "Dein Benutzername ist: ");
			msg.put("webinterface-your-pw", "Dein Passwort ist: ");
			msg.put("webinterface-login-is-temp", "Dieses Login ist Temporär!");
			msg.put("welcome", "Willkommen, ");
		} else if (lang == Language.EN) {
			msg.put("help-dialog", "Sprummlbot v" + Config.VERSION + ". Programed by " + Config.AUTHOR + ".");
			msg.put("commandslist", "Commands: [B]");
			msg.put("your-ip", "Your IP is: [B]");
			msg.put("webinterface-disalbed", "Webinterface is disabled!");
			msg.put("webinterface-your-user", "Your username is: ");
			msg.put("webinterface-your-pw", "Your password is: ");
			msg.put("webinterface-login-is-temp", "This login is temporary!");
			msg.put("welcome", "Welcome, ");
		}
	}

	public static String get(String id) {
		return msg.get(id);
	}

	public static void add(String id, String msgs) {
		msg.put(id, msgs);
	}

	private static HashMap<String, String> msg = new HashMap<>();
}
