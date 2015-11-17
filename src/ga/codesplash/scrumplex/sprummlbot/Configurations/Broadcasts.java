package ga.codesplash.scrumplex.sprummlbot.configurations;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

import ga.codesplash.scrumplex.sprummlbot.Vars;


public class Broadcasts {

	public static void load(File f) throws Exception {
		System.out.println("Updating Config File " + f.getName());
		updateCFG(f);
		Ini ini = new Ini(f);

		Section sec = ini.get("Messages");
		String[] messages = sec.getAll("msg", String[].class);
		Collections.addAll(Vars.BROADCASTS, messages);
		
		if (Vars.DEBUG == 2) {
			for (String str : ini.keySet()) {
				for (String out : ini.get(str).keySet()) {
					System.out.println("[DEBUG] [CONF] [broadcasts.ini] " + str + "." + out + ": " + ini.get(str).get(out));
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
		if (!ini.containsKey("Messages")) {
			Section sec = ini.add("Messages");
			ini.putComment("Messages", "You need to put the broadcast messages into the list below");
			sec.put("msg", "Visit our Website!");
		}
		System.out.println("Saving updated config...");
		ini.store();
		System.out.println("Done! Please setup the new Configuration Sections!");
	}
}
