package net.scrumplex.sprummlbot.configurations;

import net.scrumplex.sprummlbot.Vars;
import net.scrumplex.sprummlbot.plugins.Config;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

/**
 * Configuration class
 */
public class Broadcasts {

    /**
     * Loads Broadcasts Config File
     *
     * @param f File, which will be loaded
     * @throws IOException
     */
    public static void load(File f, boolean silent) throws IOException {
        Ini defaultIni = new Ini();
        Section defaultSec = defaultIni.add("Messages");
        defaultIni.putComment("Messages", "You need to put the broadcast messages into the list below");
        defaultSec.add("msg", "Visit our Website!");
        defaultSec.add("msg", "For Youtube send !yt to the bot!");

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

        Section sec = ini.get("Messages");
        String[] messages = sec.getAll("msg", String[].class);
        Collections.addAll(Vars.BROADCASTS, messages);
    }
}
