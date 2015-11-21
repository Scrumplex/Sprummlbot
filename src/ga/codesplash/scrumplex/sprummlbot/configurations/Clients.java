package ga.codesplash.scrumplex.sprummlbot.configurations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

import ga.codesplash.scrumplex.sprummlbot.Vars;


public class Clients {

	/**
	 * Loads clients Config File
	 * @param f
	 * File, which will be loaded
	 * @throws Exception
	 */
	public static void load(File f) throws Exception {
		System.out.println("Updating Config File " + f.getName());
		updateCFG(f);
		Ini ini = new Ini(f);
		Section sec = ini.get("Webinterface Login");
		for (String uid : sec.values()) {
			Vars.LOGINABLE.add(uid);
		}
		sec = ini.get("AFK Dont Move");
		for (String uid : sec.values()) {
			Vars.AFK_ALLOWED.add(uid);
		}
		sec = ini.get("Support Notify");
		for (String uid : sec.values()) {
			Vars.SUPPORTERS.add(uid);
		}
		sec = ini.get("Broadcast Ignore");
		for (String uid : sec.values()) {
			Vars.BROADCAST_IGNORE.add(uid);
		}
		sec = ini.get("Recording Allowed");
		for (String uid : sec.values()) {
			Vars.AFK_ALLOWED.add(uid);
		}
		sec = ini.get("Sprummlbot Notify");
		for (String uid : sec.values()) {
			Vars.NOTIFY.add(uid);
		}
		if (Vars.DEBUG == 2) {
			for (String str : ini.keySet()) {
				for (String out : ini.get(str).keySet()) {
					System.out.println("[DEBUG] [CONF] [clients.ini] " + str + "." + out + ": " + ini.get(str).get(out));
				}
			}
		}
	}

	public static void updateCFG(File f) throws IOException {
		if (!f.exists()) {
			if(!f.createNewFile()) {
				System.out.println("Could not create " + f.getName());
			}
		}
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
		System.out.println("Saving updated config...");
		ini.store();
		System.out.println("Done! Please setup the new Configuration Sections!");
	}
}
