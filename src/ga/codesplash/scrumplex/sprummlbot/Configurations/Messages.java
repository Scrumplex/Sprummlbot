package ga.codesplash.scrumplex.sprummlbot.Configurations;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

import ga.codesplash.scrumplex.sprummlbot.Vars;
import ga.codesplash.scrumplex.sprummlbot.Logger;
import ga.codesplash.scrumplex.sprummlbot.stuff.ConfigException;
import ga.codesplash.scrumplex.sprummlbot.stuff.Language;

public class Messages {

	public static Language lang = null;

	public static void setupLanguage(Language language) throws Exception {
		Logger.out("Sprummlbot language is: " + language.getID());
		lang = language;
		File f = new File("messages-" + language.getID() + ".ini");

		Logger.out("Updating Config File " + f.getName());
		updateCFG(f);

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
				|| map.containsKey("someone-is-in-support") == false || map.containsKey("unknown-command") == false
				|| map.containsKey("you-wont-be-notified") == false
				|| map.containsKey("you-will-be-notified") == false) {
			throw new ConfigException("Language File is not defined carefully!");
		}

		msg = map;

		if (lang == Language.DE) {
			msg.put("help-dialog", "Sprummlbot v" + Vars.VERSION + ". Programmiert von " + Vars.AUTHOR + ".");
			msg.put("commandslist", "Verfügbare Kommandos: [B]");
			msg.put("your-ip", "Deine öffentliche IP ist: [B]");
			msg.put("webinterface-disalbed", "Das Webinterface ist nicht aktiviert");
			msg.put("webinterface-your-user", "Dein Benutzername ist: ");
			msg.put("webinterface-your-pw", "Dein Passwort ist: ");
			msg.put("webinterface-login-is-temp", "Dieses Login ist Temporär!");
			msg.put("welcome", "Willkommen, ");
		} else if (lang == Language.EN) {
			msg.put("help-dialog", "Sprummlbot v" + Vars.VERSION + ". Programed by " + Vars.AUTHOR + ".");
			msg.put("commandslist", "Commands: [B]");
			msg.put("your-ip", "Your IP is: [B]");
			msg.put("webinterface-disalbed", "Webinterface is disabled!");
			msg.put("webinterface-your-user", "Your username is: ");
			msg.put("webinterface-your-pw", "Your password is: ");
			msg.put("webinterface-login-is-temp", "This login is temporary!");
			msg.put("welcome", "Welcome, ");
		}
	}

	public static void updateCFG(File f) throws InvalidFileFormatException, IOException {
		if(!f.exists()) {
			f.createNewFile();
		}
		Ini ini = new Ini(f);
		Map<String, String> de = new HashMap<>();
		de.put("you-were-moved-to-afk", "Du wurdest in den AFK Channel gemoved!");
		de.put("you-were-moved-back-from-afk", "Du wurdest zurück gemoved!");
		de.put("you-mustnt-record-here", "[B]Du darfst nicht aufnehmen![/B]");
		de.put("you-joined-support-channel", "Alle Teammitglieder wurden kontaktiert!");
		de.put("you-are-not-longer-in-support-queue", "Du wurdest aus der Warteschlange entfernt!");
		de.put("someone-is-in-support", "[B]Jemand is im Support Channel[/B]");
		de.put("unknown-command", "Dieser Befehl ist nicht bekannt!");
		de.put("you-wont-be-notified",
				"Du wirst nun keine Broadcast Nachrichten mehr erhalten. Dies ist nicht permanent");
		de.put("you-will-be-notified", "Du wirst von nun an wieder Broadcast Nachrichten bekommen!");

		Map<String, String> en = new HashMap<>();
		en.put("you-were-moved-to-afk", "You were moved to AFK Channel!");
		en.put("you-were-moved-back-from-afk", "You were moved back!");
		en.put("you-mustnt-record-here", "[B]You must not record here![/B]");
		en.put("you-joined-support-channel", "The team got a message!");
		en.put("you-are-not-longer-in-support-queue", "You are not longer in the queue!");
		en.put("someone-is-in-support", "[B]Someone needs support[/B]");
		en.put("unknown-command", "Unknown Command!");
		en.put("you-wont-be-notified", "You won't get broadcast messages anymore. This is not permanent!");
		en.put("you-will-be-notified", "You will get broadcast messages.");
		if (!ini.containsKey("Global")) {
			ini.add("Global");
		}
		Section sec = ini.get("Global");
		if (lang == Language.DE) {
			for (String key : de.keySet()) {
				sec.put(key, de.get(key));
			}
		} else {
			for (String key : en.keySet()) {
				sec.put(key, en.get(key));
			}
		}
		Logger.out("Saving updated config...");
		ini.store();
		Logger.out("Done! Please setup the new Configuration Sections!");
	}

	public static String get(String id) {
		return msg.get(id);
	}

	public static void add(String id, String msgs) {
		msg.put(id, msgs);
	}

	private static HashMap<String, String> msg = new HashMap<>();
}
