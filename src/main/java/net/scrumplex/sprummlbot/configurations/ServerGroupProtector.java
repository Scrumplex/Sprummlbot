package net.scrumplex.sprummlbot.configurations;

import net.scrumplex.sprummlbot.Vars;
import net.scrumplex.sprummlbot.plugins.Config;
import net.scrumplex.sprummlbot.tools.EasyMethods;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ServerGroupProtector {

    static void load(File f, boolean silent) throws IOException {
        if (!silent)
            System.out.println("Checking " + f.getName() + " if it is outdated...");
        Config conf = new Config(f).setDefaultConfig(new Ini()).compare();
        if (conf.wasChanged()) {
            if (!silent)
                System.out.println(f.getName() + " was updated.");
        } else {
            if (!silent)
                System.out.println(f.getName() + " was up to date.");
        }
        final Ini ini = conf.getIni();
        for (String secname : ini.keySet()) {
            if (!EasyMethods.isInteger(secname)) {
                System.err.println(secname + " in groupprotect.ini will be ignored (not a valid group id)");
            } else {
                List<String> uids = new ArrayList<>();
                Section sec = ini.get(secname);
                for (String ids : sec.getAll("uid"))
                    if (ids.contains(","))
                        Collections.addAll(uids, ids.split(","));
                    else
                        uids.add(ids);
                Vars.GROUPPROTECT_LIST.put(Integer.valueOf(secname), uids);
            }
        }
    }
}
