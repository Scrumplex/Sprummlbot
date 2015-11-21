package ga.codesplash.scrumplex.sprummlbot.configurations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;


import ga.codesplash.scrumplex.sprummlbot.Vars;
import ga.codesplash.scrumplex.sprummlbot.stuff.EasyMethods;

public class ServerGroupProtector {

    /**
     * Loads groupprotect Config File
     * @param f
     * File, which will be loaded
     * @throws IOException
     */
    public static void load(File f) throws IOException {
        updateCFG(f);
        Ini ini = new Ini(f);
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

    public static void updateCFG(File f) throws IOException {
        if (!f.exists()) {
            if(!f.createNewFile()) {
                System.out.println("Could not create " + f.getName());
            }
        }


        Ini ini = new Ini(f);
        if (!ini.containsKey("0")) {
            Section sec = ini.add("0");
            sec.put("uid", "UID1");
        }
        ini.putComment("0", "Change the 0 with your group id. CAUTION: Sprummlbot will remova everybody from that group, who is not in there!");
    }
}
