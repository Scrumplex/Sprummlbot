package net.scrumplex.sprummlbot.configurations;

import net.scrumplex.sprummlbot.Vars;
import net.scrumplex.sprummlbot.plugins.Config;
import net.scrumplex.sprummlbot.tools.PermissionGroup;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;

public class Permissions {

    public static void load(File f, boolean silent) throws IOException {
        Ini defaultIni = getDefaultIni();
        if (!silent)
            System.out.println("Checking " + f.getName() + " if it is outdated...");
        Config conf = new Config(f).setDefaultConfig(defaultIni).compare();
        if (conf.wasChanged()) {
            if (!silent)
                System.out.println(f.getName() + " was updated.");
        } else {
            if (!silent)
                System.out.println(f.getName() + " was up to date.");
        }
        final Ini ini = conf.getIni();
        for (Profile.Section sec : ini.values()) {
            PermissionGroup group = new PermissionGroup(sec.getName());
            if (sec.containsKey("uid"))
                for (String uid : sec.getAll("uid"))
                    group.addClient(uid);

            if (sec.containsKey("group"))
                for (int serverGroup : sec.getAll("group", int[].class))
                    group.addGroup(serverGroup);

            if (sec.containsKey("inherit"))
                for (String permGroup : sec.getAll("inherit"))
                    group.addIncludingPermissionGroup(permGroup);
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
        defaultSec.add("group", 8);
        defaultSec.add("inherit", "Admins");

        defaultSec = ini.add("VIPs");
        defaultSec.add("uid", "uid3");
        defaultSec.add("uid", "uid4");
        defaultSec.add("group", 9);
        defaultSec.add("inherit", "Supporters");
        return ini;
    }

}
