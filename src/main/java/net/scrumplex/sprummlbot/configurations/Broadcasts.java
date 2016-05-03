package net.scrumplex.sprummlbot.configurations;

import net.scrumplex.sprummlbot.Vars;
import net.scrumplex.sprummlbot.plugins.Config;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

class Broadcasts {

    static void load(File f, boolean silent) throws IOException {
        Ini defaultIni = new Ini();
        Section defaultSec = defaultIni.add("Messages");
        defaultIni.putComment("Messages", "You need to put the broadcast messages into the list below");
        defaultSec.add("msg", "Visit our Website!");
        defaultSec.add("msg", "For Youtube send !yt to the bot!");
        Config conf = new Config(f).setDefaultConfig(defaultIni).compare();
        if (conf.wasChanged() && !silent)
            System.out.println("[Config] " + f.getName() + " has been updated.");
        final Ini ini = conf.getIni();

        Section sec = ini.get("Messages");
        String[] messages = sec.getAll("msg", String[].class);
        Collections.addAll(Vars.BROADCASTS, messages);
    }
}
