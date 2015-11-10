package ga.codesplash.scrumplex.sprummlbot.Configurations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

import ga.codesplash.scrumplex.sprummlbot.Logger;
import ga.codesplash.scrumplex.sprummlbot.Vars;
import ga.codesplash.scrumplex.sprummlbot.stuff.EasyMethods;

public class ServerGroupProtector {

	public static void load(File f) throws InvalidFileFormatException, IOException {
		updateCFG(f);
		Ini ini = new Ini(f);
		for (String secname : ini.keySet()) {
			if (!EasyMethods.isInteger(secname)) {
				Logger.warn(secname + " in groupprotect.ini will be ignored (not a valid group id)");
			} else {
				Section sec = ini.get(secname);
				for (String uid : sec.values()) {
					List<String> uids = new ArrayList<>();
					if (Vars.GROUPPROTECT_LIST.get(Integer.valueOf(secname)) != null) {
						uids = Vars.GROUPPROTECT_LIST.get(Integer.valueOf(secname));
					}
					uids.add(uid);
					Vars.GROUPPROTECT_LIST.put(Integer.valueOf(secname), uids);
				}
			}
		}
		if (Vars.DEBUG == 2) {
			for (String str : ini.keySet()) {
				for (String out : ini.get(str).keySet()) {
					Logger.out("[DEBUG] [CONF] [groupprotect.ini] " + str + "." + out + ": " + ini.get(str).get(out));
				}
			}
		}
	}

	public static void updateCFG(File f) throws InvalidFileFormatException, IOException {
		if (!f.exists()) {
			f.createNewFile();
		}
		Ini ini = new Ini(f);
		if (!ini.containsKey("6")) {
			Section sec = ini.add("6");
			sec.put("uid", "UID1");
		}
	}
}
