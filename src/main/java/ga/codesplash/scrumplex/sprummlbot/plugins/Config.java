package ga.codesplash.scrumplex.sprummlbot.plugins;

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
        for (Profile.Section defaultSec : defaultIni.values()) {
            if (!ini.containsKey(defaultSec.getName())) {
                ini.put(defaultSec.getName(), defaultSec);
                changed = true;
            }
            Profile.Section originalSection = ini.get(defaultSec.getName());
            for (String key : defaultSec.keySet()) {
                if (!originalSection.containsKey(key)) {
                    originalSection.add(key, defaultSec.get(key));
                    changed = true;
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
