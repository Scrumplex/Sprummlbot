package ga.codesplash.scrumplex.sprummlbot.configurations;

import ga.codesplash.scrumplex.sprummlbot.Vars;
import ga.codesplash.scrumplex.sprummlbot.plugins.Config;
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
     * @throws IOException
     */
    public static void load(File f) throws IOException {

        Ini defaultIni = new Ini();

        defaultIni.add("Webinterface Login");
        defaultIni.add("AFK Dont Move");
        defaultIni.add("Support Notify");
        defaultIni.add("Recording Allowed");
        defaultIni.add("Broadcast Ignore");
        defaultIni.add("Sprummlbot Notify");
        defaultIni.add("Can Use VPN");
        for (Section sec : defaultIni.values()) {
            sec.put("uid", "PUT UID HERE");
            sec.add("uid", "PUT UID2 HERE");
        }

        System.out.println("Checking " + f.getName() + " if it is outdated...");
        Config conf = new Config(f).setDefaultConfig(defaultIni).compare();
        if(conf.wasChanged()) {
            System.out.println(f.getName() + " was updated.");
        } else {
            System.out.println(f.getName() + " was up to date.");
        }
        final Ini ini = conf.getIni();

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
        sec = ini.get("Can Use VPN");
        for (String uid : sec.values()) {
            Vars.VPNCHECKER_WL.add(uid);
        }
        if (Vars.DEBUG == 2) {
            for (String str : ini.keySet()) {
                for (String out : ini.get(str).keySet()) {
                    System.out.println("[DEBUG] [CONF] [clients.ini] " + str + "." + out + ": " + ini.get(str).get(out));
                }
            }
        }
    }
}
