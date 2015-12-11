package ga.codesplash.scrumplex.sprummlbot.configurations;

import ga.codesplash.scrumplex.sprummlbot.Vars;
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
     * @throws Exception
     */
    public static void load(File f) throws Exception {
        final Ini ini = updateCFG(f);
        Section sec = ini.get("Messages");
        String[] messages = sec.getAll("msg", String[].class);
        Collections.addAll(Vars.BROADCASTS, messages);

        if (Vars.DEBUG == 2) {
            for (String str : ini.keySet()) {
                for (String out : ini.get(str).keySet()) {
                    System.out.println("[DEBUG] [CONF] [broadcasts.ini] " + str + "." + out + ": " + ini.get(str).get(out));
                }
            }
        }
    }

    public static Ini updateCFG(File configFile) throws IOException {
        System.out.println("Updating Config File " + configFile.getName());
        boolean changed = false;
        if (!configFile.exists()) {
            if (!configFile.createNewFile()) {
                System.out.println("Could not create " + configFile.getName());
            }
        }
        Ini ini = new Ini(configFile);
        Ini defaultIni = new Ini();
        Section defaultSec = defaultIni.add("Messages");
        defaultIni.putComment("Messages", "You need to put the broadcast messages into the list below");
        defaultSec.add("msg", "Visit our Website!");
        defaultSec.add("msg", "For Youtube send !yt to the bot!");

        for (Section section : defaultIni.values()) {
            if (!ini.containsKey(section.getName())) {
                ini.put(section.getName(), section);
                changed = true;
            }
            Section sec = ini.get(section.getName());
            for (String key : section.keySet()) {
                if (!sec.containsKey(key)) {
                    sec.put(key, section.get(key));
                    changed = true;
                }
            }
        }
        if (changed) {
            System.out.println("Saving updated config...");
            ini.store();
            System.out.println("Done! Please setup the new Configuration Sections!");
        } else {
            System.out.println("Done! Nothing was changed :) Have fun!");
        }
        return ini;
    }
}
