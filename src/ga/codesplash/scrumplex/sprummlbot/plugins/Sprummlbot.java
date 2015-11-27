package ga.codesplash.scrumplex.sprummlbot.plugins;

import com.github.theholywaffle.teamspeak3.TS3Api;

import ga.codesplash.scrumplex.sprummlbot.SprummlbotLoader;
import ga.codesplash.scrumplex.sprummlbot.Vars;
import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;

public class Sprummlbot {

	/**
	 * @return
	 * Returns main com.github.theholywaffle.teamspeak3.TS3Api instance.
     */
	public static TS3Api getAPI() {
		return Vars.API;
	}

	/**
	 * @return
	 * Returns main PluginLoader instance
     */
	public static PluginLoader getPluginLoader() {
		return SprummlbotLoader.pl;
	}


	/**
	 * @return
	 * Returns main PluginManager instance
	 */
	public static PluginManager getPluginManager() {
		return SprummlbotLoader.pm;
	}

	/**
	 * Creates / Reads a config file for the plugin
	 * @param sprummlPlugin
	 * For identifying multiple plugins
	 * @return
	 * Returns Config (Documentation: http://ini4j.sourceforge.net/)
	 * @throws IOException
     */
	public static Ini getConfig(SprummlPlugin sprummlPlugin) throws IOException {
		File dir = new File("plugins/" + getPluginManager().getPluginBySprummlPlugin(sprummlPlugin).getName());
		File f = new File(dir, "config.ini");
		if(!dir.exists()) if (!dir.mkdirs()) return null;
		if(!f.exists()) if (!f.createNewFile()) return null;
		return new Ini(f);
	}
}
