package ga.codesplash.scrumplex.sprummlbot.configurations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;


import ga.codesplash.scrumplex.sprummlbot.Vars;
import ga.codesplash.scrumplex.sprummlbot.tools.EasyMethods;

/**
 * Configuration class
 */
class ServerGroupProtector {

    /**
     * Loads groupprotect Config File
     *
     * @param f File, which will be loaded
     * @throws IOException
     */
    public static void load(File f) throws IOException {
        Ini ini = new Ini(f);
        updateCFG(ini);
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

    public static void updateCFG(Ini ini) throws IOException {
        if (!ini.getFile().exists()) {
            if (!ini.getFile().createNewFile()) {
                System.out.println("Could not create " + ini.getFile().getName());
            }
        }
    }
}
