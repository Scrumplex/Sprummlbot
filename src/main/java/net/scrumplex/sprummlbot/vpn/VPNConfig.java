package net.scrumplex.sprummlbot.vpn;

import net.scrumplex.sprummlbot.tools.Exceptions;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;

public class VPNConfig {

    private Ini ini;
    private Profile.Section sec;

    public VPNConfig(File f) throws IOException {
        if (!f.exists())
            f.createNewFile();
        ini = new Ini(f);
        sec = ini.get("Saved IPs");
        validate();
    }


    private void validate() {
        if (sec == null) {
            sec = ini.add("Saved IPs");
            save();
        }
    }

    private void save() {
        try {
            ini.store();
        } catch (IOException e) {
            Exceptions.handle(e, "VPNChecker couldn't save config!", false);
        }
    }

    boolean isBlocked(String ip) {
        validate();
        return sec.containsValue(ip);
    }

    void add(String ip) {
        validate();
        if (!sec.containsValue(ip)) {
            sec.add("ip", ip);
            save();
        }
    }
}

