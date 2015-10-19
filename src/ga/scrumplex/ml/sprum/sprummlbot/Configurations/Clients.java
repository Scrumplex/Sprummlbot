package ga.scrumplex.ml.sprum.sprummlbot.Configurations;

import java.io.File;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import ga.scrumplex.ml.sprum.sprummlbot.Config;
import ga.scrumplex.ml.sprum.sprummlbot.stuff.ConfigException;

public class Clients {

	public static void load(File f) throws Exception {
		Ini ini = new Ini(f);
		if (!ini.containsKey("Webinterface Login")) {
			throw new ConfigException("Webinterface Login section was not defined!");
		}
		if (!ini.containsKey("AFK Dont Move")) {
			throw new ConfigException("AFK Dont Move section was not defined!");
		}
		if (!ini.containsKey("Support Notify")) {
			throw new ConfigException("Support Notify section was not defined!");
		}
		if (!ini.containsKey("Recording Allowed")) {
			throw new ConfigException("Recording Allowed section was not defined!");
		}
		if (!ini.containsKey("Sprummlbot Notify")) {
			throw new ConfigException("Sprummlbot Notify section was not defined!");
		}
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
}
