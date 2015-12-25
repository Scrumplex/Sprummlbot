package ga.codesplash.scrumplex.sprummlbot.vpn;

import ga.codesplash.scrumplex.sprummlbot.tools.Exceptions;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
            Exceptions.handle(e, "VPNChacker couldn't save config!", false);
        }
    }

    public List<String> get() {
        validate();
        return new ArrayList<>(sec.values());
    }

    public void add(String ip) {
        validate();
        if (!sec.containsValue(ip)) {
            sec.add("ip", ip);
            save();
        }
    }
}

