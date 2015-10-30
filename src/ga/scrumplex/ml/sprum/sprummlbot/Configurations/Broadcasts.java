package ga.scrumplex.ml.sprum.sprummlbot.Configurations;

import java.io.File;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import ga.scrumplex.ml.sprum.sprummlbot.Config;
import ga.scrumplex.ml.sprum.sprummlbot.stuff.ConfigException;

public class Broadcasts {

	public static void load(File f) throws Exception {
		Ini ini = new Ini(f);
		if (!ini.containsKey("Messages")) {
			throw new ConfigException("Messages section was not defined!");
		}
		Section sec = ini.get("Messages");
		String[] messages = sec.getAll("msg", String[].class);
		for (String msg : messages) {
			Config.BROADCASTS.add(msg);
		}
	}

}
