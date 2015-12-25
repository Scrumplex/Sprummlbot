package ga.codesplash.scrumplex.sprummlbot.plugins;

import ga.codesplash.scrumplex.sprummlbot.Main;
import ga.codesplash.scrumplex.sprummlbot.tools.SprummlbotOutStream;

/**
 * This class is for plugins.
 * It will give some important things like TS3Api or the Confgi of the Plugin.
 */
public class Sprummlbot {

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
