package net.scrumplex.sprummlbot.configurations;

import net.scrumplex.sprummlbot.Vars;
import net.scrumplex.sprummlbot.plugins.Config;
import net.scrumplex.sprummlbot.tools.EasyMethods;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;

class ChannelStats {

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
        for (Profile.Section sec : ini.values()) {
            if(!EasyMethods.isInteger(sec.getName())) {
                System.err.println(sec.getName() + " in channelstats.ini is not valid and will be ignored!");
                continue;
            }
            int channelId = Integer.parseInt(sec.getName());
            String name = sec.get("channel-name");
            Vars.CHANNELSTATS.put(channelId, name);
        }
    }
}
