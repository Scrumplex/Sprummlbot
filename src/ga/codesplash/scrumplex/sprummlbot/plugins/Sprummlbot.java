package ga.codesplash.scrumplex.sprummlbot.plugins;

import com.github.theholywaffle.teamspeak3.TS3Api;

import ga.codesplash.scrumplex.sprummlbot.SprummlbotLoader;
import ga.codesplash.scrumplex.sprummlbot.Vars;
import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;

public class Sprummlbot {

	public static TS3Api getAPI() {
		return Vars.API;
	}	
	
	public static PluginLoader getPluginLoader() {
		return SprummlbotLoader.pl;
	}

	public static Ini getConfig(SprummlPlugin plugin) throws IOException {
		File dir = new File("plugins/" + plugin.getName());
		File f = new File(dir, "config.ini");
		if(!dir.exists()) if (!dir.mkdirs()) return null;
		if(!f.exists()) if (!f.createNewFile()) return null;
		return new Ini(f);
	}
}
