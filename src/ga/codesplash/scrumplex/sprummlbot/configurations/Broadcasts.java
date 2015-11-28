package ga.codesplash.scrumplex.sprummlbot.configurations;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

import ga.codesplash.scrumplex.sprummlbot.Vars;

/**
 * Configuration class
 */
public class Broadcasts {

	/**
	 * Loads Broadcasts Config File
	 * @param f
	 * File, which will be loaded
	 * @throws Exception
     */
	public static void load(File f) throws Exception {
		System.out.println("Updating Config File " + f.getName());
        final Ini ini = new Ini(f);
		updateCFG(ini);

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

	/**
	 * Updates Configs
	 * @param ini
	 * Ini which will be updated
	 * @throws IOException
     */
	public static void updateCFG(Ini ini) throws IOException {
		if (!ini.getFile().exists()) {
			if(!ini.getFile().createNewFile()) {
				System.out.println("Could not create " + ini.getFile().getName());
			}
		}
		if (!ini.containsKey("Messages")) {
			Section sec = ini.add("Messages");
			ini.putComment("Messages", "You need to put the broadcast messages into the list below");
			sec.put("msg", "Visit our Website!");
            System.out.println("Saving updated config...");
            ini.store();
            System.out.println("Done! Please setup the new Configuration Sections!");
		}
	}
}
