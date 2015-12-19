package ga.codesplash.scrumplex.sprummlbot.plugins;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import ga.codesplash.scrumplex.sprummlbot.Main;
import ga.codesplash.scrumplex.sprummlbot.Vars;
import ga.codesplash.scrumplex.sprummlbot.tools.SprummlbotOutStream;
import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;

/**
 * This class is for plugins.
 * It will give some important things like TS3Api or the Confgi of the Plugin.
 */
public class Sprummlbot {

    /**
     * @return Returns main com.github.theholywaffle.teamspeak3.TS3ApiAsync instance.
     */
    public TS3ApiAsync getAPI() {
        return Vars.API;
    }

    /**
     * @return Returns main PluginLoader instance
     */
    public PluginLoader getPluginLoader() {
        return Main.pluginLoader;
    }


    /**
     * @return Returns main PluginManager instance
     */
    public PluginManager getPluginManager() {
        return Main.pluginManager;
    }

    /**
     * @return Returns the Print stream for System.out
     */
    public SprummlbotOutStream getConsole() {
        return Main.out;
    }
}
