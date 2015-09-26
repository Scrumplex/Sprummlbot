package ga.scrumplex.ml.sprum.sprummlbot;

import java.util.ArrayList;
import java.util.HashMap;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

public class Config {
	public static TS3Query query = null;	
	public static TS3Api api = null;	
	public static HashMap<Integer, Integer> idle = new HashMap<Integer, Integer>();
	public static ArrayList<Integer> support = new ArrayList<Integer>();
	
	public static String version = "0.0.7";
	public static int versionid = 6;
	public static String author = "Scrumplex";
	
	public static String server = "google.de";
	public static String[] login = {"serveradmin", "password"};
	public static ArrayList<String> admins = new ArrayList<String>();
	public static int vserver = 1;
	public static int qID;
	public static String botname = "Sprummlbot";
	public static int webport = 80;
	public static int port = 10011;
	
	public static boolean afk = true;
	public static ArrayList<Integer> deniedchannels = new ArrayList<>();
	public static int afkchannelid = 1;
	public static int afkidle = 600*1000;
	
	public static boolean supports = true;
	public static int supportchannelid = 0;
	
	public static boolean updater;
	public static int debug = 2;
	public static boolean moveadmins;
	public static int timertick;
	
	public static Client getClientbyID(int id) {
		return api.getClientInfo(id);
	}
}
