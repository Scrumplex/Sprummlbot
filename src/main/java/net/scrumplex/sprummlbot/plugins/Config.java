package net.scrumplex.sprummlbot.plugins;

import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;

public class Config {

    private final File f;
    private Ini ini = null;
    private Ini defaultIni = null;
    private boolean changed = false;

    public Config(File f) throws IOException {
        this.f = f;
        if (!f.exists()) {
            f.createNewFile();
            changed = true;
        }
        reload();
    }

    public Config setDefaultConfig(Ini ini) {
        defaultIni = ini;
        return this;
    }

    public Config compare() throws IOException {
        if (defaultIni == null) {
            throw new NullPointerException("Default Config not defined");
        }
        for (Profile.Section section : defaultIni.values()) {
            if (!ini.containsKey(section.getName())) {
                ini.put(section.getName(), section);
                ini.putComment(section.getName(), defaultIni.getComment(section.getName()));
                changed = true;
            } else {
                for (String key : section.keySet()) {
                    Profile.Section realSection = ini.get(section.getName());
                    if (!realSection.containsKey(key)) {
                        realSection.put(key, section.get(key));
                        realSection.putComment(key, section.getComment(key));
                        changed = true;
                    }
                }
            }
        }
        if (wasChanged())
            ini.store();
        return this;
    }

    public boolean wasChanged() {
        return changed;
    }


    public void reload() throws IOException {
        if (ini != null)
            ini.store();
        ini = new Ini(f);
    }

    public Ini getIni() {
        return ini;
    }

}
