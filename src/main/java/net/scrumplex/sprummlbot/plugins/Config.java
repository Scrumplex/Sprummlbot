package net.scrumplex.sprummlbot.plugins;

import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;

public class Config {

    private Ini ini = null;
    private Ini defaultIni = null;
    private boolean changed = false;

    /**
     * This will create a config file (if it doesn't exist already) and load it.
     *
     * @param f File which will be created and loaded.
     * @throws IOException
     */
    public Config(File f) throws IOException {
        if (!f.exists()) {
            f.createNewFile();
            changed = true;
        }
        ini = new Ini(f);
    }

    /**
     * With this method you can set a defaul config. See compare() for more information
     *
     * @param ini The default ini
     * @return Returns this class for chains.
     */
    public Config setDefaultConfig(Ini ini) {
        defaultIni = ini;
        return this;
    }

    /**
     * This method will compare the config file and the default config.
     * If the config file doesnt contain a section or an child element. it will be added.
     *
     * @return Returns this class for chains.
     * @throws IOException
     */
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

    public Ini getIni() {
        return ini;
    }

}
