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
    public static TS3ApiAsync getAPI() {
        return Vars.API;
    }

    /**
     * @return Returns main PluginLoader instance
     */
    public static PluginLoader getPluginLoader() {
        return Main.pluginLoader;
    }


    /**
     * @return Returns main PluginManager instance
     */
    public static PluginManager getPluginManager() {
        return Main.pluginManager;
    }

    /**
     * Creates / Reads a config file for the plugin
     *
     * @param sprummlPlugin For identifying multiple plugins
     * @return Returns Config (Documentation: http://ini4j.sourceforge.net/)
     * @throws IOException
     */
    public static Ini getConfig(SprummlPlugin sprummlPlugin) throws IOException {
        File dir = new File("plugins/" + getPluginManager().getPluginBySprummlPlugin(sprummlPlugin).getName());
        File f = new File(dir, "config.ini");
        if (!dir.exists()) if (!dir.mkdirs()) return null;
        if (!f.exists()) if (!f.createNewFile()) return null;
        return new Ini(f);
    }


    /**
     * @return Returns the Print stream for System.out
     */
    public static SprummlbotOutStream getConsole() {
        return Main.out;
    }
}
