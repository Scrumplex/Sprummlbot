package ga.codesplash.scrumplex.sprummlbot.configurations;

import ga.codesplash.scrumplex.sprummlbot.Vars;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import java.io.File;
import java.io.IOException;


/**
 * Configuration class
 */
public class Clients {

    /**
     * Loads clients Config File
     *
     * @param f File, which will be loaded
     * @throws Exception
     */
    public static void load(File f) throws Exception {
        if (!f.exists()) {
            if (!f.createNewFile()) {
                System.out.println("Could not create " + f.getName());
            }
        }
        Ini ini = updateCFG(f);
        Section sec = ini.get("Webinterface Login");
        for (String uid : sec.values()) {
            Vars.LOGINABLE.add(uid);
        }
        sec = ini.get("AFK Dont Move");
        for (String uid : sec.values()) {
            Vars.AFK_ALLOWED.add(uid);
        }
        sec = ini.get("Support Notify");
        for (String uid : sec.values()) {
            Vars.SUPPORTERS.add(uid);
        }
        sec = ini.get("Broadcast Ignore");
        for (String uid : sec.values()) {
            Vars.BROADCAST_IGNORE.add(uid);
        }
        sec = ini.get("Recording Allowed");
        for (String uid : sec.values()) {
            Vars.AFK_ALLOWED.add(uid);
        }
        sec = ini.get("Sprummlbot Notify");
        for (String uid : sec.values()) {
            Vars.NOTIFY.add(uid);
        }
        if (Vars.DEBUG == 2) {
            for (String str : ini.keySet()) {
                for (String out : ini.get(str).keySet()) {
                    System.out.println("[DEBUG] [CONF] [clients.ini] " + str + "." + out + ": " + ini.get(str).get(out));
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

        defaultIni.add("Webinterface Login");
        defaultIni.add("AFK Dont Move");
        defaultIni.add("Support Notify");
        defaultIni.add("Recording Allowed");
        defaultIni.add("Broadcast Ignore");
        defaultIni.add("Sprummlbot Notify");


        for (Section section : defaultIni.values()) {
            if (!ini.containsKey(section.getName())) {
                ini.put(section.getName(), section);
                Section sec = ini.get(section.getName());
                sec.put("uid", "UID1");
                sec.add("uid", "UID2");
                changed = true;
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
