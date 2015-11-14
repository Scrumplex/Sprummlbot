package ga.codesplash.scrumplex.sprummlbot.plugins;

import com.github.theholywaffle.teamspeak3.TS3Api;

import ga.codesplash.scrumplex.sprummlbot.SprummlbotLoader;
import ga.codesplash.scrumplex.sprummlbot.Vars;

public class Sprummlbot {

	public static TS3Api getAPI() {
		return Vars.API;
	}	
	
	public static PluginLoader getPluginLoader() {
		return SprummlbotLoader.pl;
	}
}
