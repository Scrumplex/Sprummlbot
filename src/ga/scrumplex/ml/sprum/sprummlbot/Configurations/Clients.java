package ga.scrumplex.ml.sprum.sprummlbot.Configurations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

import ga.scrumplex.ml.sprum.sprummlbot.Config;
import ga.scrumplex.ml.sprum.sprummlbot.Logger;

public class Clients {

	public static void load(File f) throws Exception {
		if (!f.exists()) {
			f.createNewFile();
		}
		Logger.out("Updating Config File " + f.getName());
		updateCFG(f);
		Ini ini = new Ini(f);
		Section sec = ini.get("Webinterface Login");
		String[] uids = sec.getAll("uid", String[].class);
		for (String uid : uids) {
			Config.LOGINABLE.add(uid);
		}
		sec = ini.get("AFK Dont Move");
		uids = sec.getAll("uid", String[].class);
		for (String uid : uids) {
			Config.AFK_ALLOWED.add(uid);
		}
		sec = ini.get("Support Notify");
		uids = sec.getAll("uid", String[].class);
		for (String uid : uids) {
			Config.SUPPORTERS.add(uid);
		}
		sec = ini.get("Broadcast Ignore");
		uids = sec.getAll("uid", String[].class);
		for (String uid : uids) {
			Config.BROADCAST_IGNORE.add(uid);
		}
		sec = ini.get("Recording Allowed");
		uids = sec.getAll("uid", String[].class);
		for (String uid : uids) {
			Config.AFK_ALLOWED.add(uid);
		}
		sec = ini.get("Sprummlbot Notify");
		uids = sec.getAll("uid", String[].class);
		for (String uid : uids) {
			Config.NOTIFY.add(uid);
		}
	}

	public static void updateCFG(File f) throws InvalidFileFormatException, IOException {
		Ini ini = new Ini(f);
		List<String> list = new ArrayList<>();
		list.add("Webinterface Login");
		list.add("AFK Dont Move");
		list.add("Support Notify");
		list.add("Recording Allowed");
		list.add("Broadcast Ignore");
		list.add("Sprummlbot Notify");
		for (String secname : list) {
			if (!ini.containsKey(secname)) {
				Section sec = ini.add(secname);
				sec.put("uid", "UID1");
				sec.add("uid", "UID2");
			}
		}
		Logger.out("Saving updated config...");
		ini.store();
		Logger.out("Done! Please setup the new Configuration Sections!");
	}
}
