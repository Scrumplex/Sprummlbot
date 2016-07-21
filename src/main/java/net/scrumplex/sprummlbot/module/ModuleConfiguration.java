package net.scrumplex.sprummlbot.module;

import net.scrumplex.sprummlbot.Sprummlbot;
import net.scrumplex.sprummlbot.plugins.Config;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;

public class ModuleConfiguration {

    private final Config conf;

    public ModuleConfiguration(File f) throws IOException {
        this.conf = new Config(f);
    }

    public void findModules() throws ModuleInitException {
        Ini ini = conf.getIni();
        for (Profile.Section sec : ini.values()) {
            if (sec.getName().toLowerCase().startsWith("module_") && sec.containsKey("type")) {
                Sprummlbot.getSprummlbot().getModuleManager().handleConfigSection(sec);
            }
        }
    }

}
