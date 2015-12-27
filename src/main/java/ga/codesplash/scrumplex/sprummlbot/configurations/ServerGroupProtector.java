package ga.codesplash.scrumplex.sprummlbot.configurations;

import ga.codesplash.scrumplex.sprummlbot.Vars;
import ga.codesplash.scrumplex.sprummlbot.plugins.Config;
import ga.codesplash.scrumplex.sprummlbot.tools.EasyMethods;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration class
 */
public class ServerGroupProtector {

    /**
     * Loads groupprotect Config File
     *
     * @param f File, which will be loaded
     * @throws IOException
     */
    public static void load(File f) throws IOException {
        System.out.println("Checking " + f.getName() + " if it is outdated...");
        Config conf = new Config(f).setDefaultConfig(new Ini()).compare();
        if (conf.wasChanged()) {
            System.out.println(f.getName() + " was updated.");
        } else {
            System.out.println(f.getName() + " was up to date.");
        }
        final Ini ini = conf.getIni();
        for (String secname : ini.keySet()) {
            if (!EasyMethods.isInteger(secname)) {
                System.out.println(secname + " in groupprotect.ini will be ignored (not a valid group id)");
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
                    System.out.println("[DEBUG] [CONF] [groupprotect.ini] " + str + "." + out + ": " + ini.get(str).get(out));
                }
            }
        }
    }
}
