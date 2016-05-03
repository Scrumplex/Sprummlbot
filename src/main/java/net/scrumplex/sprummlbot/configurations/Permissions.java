package net.scrumplex.sprummlbot.configurations;

import net.scrumplex.sprummlbot.Vars;
import net.scrumplex.sprummlbot.plugins.Config;
import net.scrumplex.sprummlbot.wrapper.PermissionGroup;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;

class Permissions {

    static void load(File f, boolean silent) throws IOException {
        Ini defaultIni = getDefaultIni();
        Config conf = new Config(f).setDefaultConfig(defaultIni).compare();
        if (conf.wasChanged() && !silent)
            System.out.println("[Config] " + f.getName() + " has been updated.");
        final Ini ini = conf.getIni();
        for (Profile.Section sec : ini.values()) {
            PermissionGroup group = new PermissionGroup(sec.getName());
            if (sec.containsKey("uid"))
                for (String ids : sec.getAll("uid"))
                    if (ids.contains(","))
                        for (String id : ids.split(","))
                            group.addClient(id);
                    else
                        group.addClient(ids);
            if (sec.containsKey("group"))
                for (String ids : sec.getAll("group"))
                    if (ids.contains(","))
                        for (String id : ids.split(","))
                            group.addGroup(Integer.parseInt(id));
                    else
                        group.addGroup(Integer.parseInt(ids));
            if (sec.containsKey("inherit"))
                for (String ids : sec.getAll("inherit"))
                    if (ids.contains(","))
                        for (String id : ids.split(","))
                            group.addIncludingPermissionGroup(id);
                    else
                        group.addIncludingPermissionGroup(ids);

            Vars.PERMGROUPS.put(sec.getName(), group);
        }
    }

    private static Ini getDefaultIni() {
        Ini ini = new Ini();
        Profile.Section defaultSec = ini.add("Admins");
        defaultSec.add("uid", "uid1");
        defaultSec.add("group", 6);

        defaultSec = ini.add("Supporters");
        defaultSec.add("uid", "uid2");
        defaultSec.add("group", 6);
        defaultSec.add("inherit", "Admins");

        defaultSec = ini.add("VIPs");
        defaultSec.add("uid", "uid3");
        defaultSec.add("uid", "uid4");
        defaultSec.add("group", 6);
        defaultSec.add("inherit", "Supporters");
        return ini;
    }

}
