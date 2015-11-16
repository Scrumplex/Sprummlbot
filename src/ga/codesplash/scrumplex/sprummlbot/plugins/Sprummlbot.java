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

	public static Ini createConfig(SprummlPlugin plugin) throws IOException {
		File f = new File("plugins/" + plugin.getName(), "config.ini");
		return new Ini(f);
	}
}
