package ga.codesplash.scrumplex.sprummlbot;

import java.util.ArrayList;
import java.util.HashMap;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Query;

public class Config {
	public static TS3Query QUERY = null;
	public static TS3Api API = null;
	public static HashMap<String, Integer> INAFK = new HashMap<String, Integer>();
	public static ArrayList<String> INSUPPORT = new ArrayList<String>();

	public static String VERSION = "0.2.5";
	public static int BUILDID = 25;
	public static String AUTHOR = "Scrumplex";

	public static String SERVER = "";
	public static String[] LOGIN = { "", "" };
	public static int SERVERID = 1;
	public static int QID;
	public static String NICK = "Sprummlbot";
	public static int PORT_WI = 9911;
	public static int PORT_SQ = 10011;

	public static boolean AFK_ENABLED = true;
	public static ArrayList<Integer> AFKALLOWED = new ArrayList<>();
	public static int AFKCHANNELID = 0;
	public static int AFKTIME = 600000;

	public static boolean SUPPORT_ENABLED = true;
	public static int SUPPORTCHANNELID = 0;

	public static boolean ANTIREC_ENABLED = true;

	public static boolean BRIDGE_ENABLED = true;
	public static boolean BRIDGE_EVENTS = true;
	public static int PORT_BRIDGE = 9944;

	public static boolean BROADCAST_ENABLED = true;
	public static ArrayList<String> BROADCASTS = new ArrayList<>();
	public static ArrayList<String> BROADCAST_IGNORE = new ArrayList<>();
	public static Integer BROADCAST_INTERVAL = 300;

	public static boolean UPDATER_ENABLED = true;
	public static int DEBUG = 0;
	public static int TIMERTICK = 4000;

	public static ArrayList<String> SUPPORTERS = new ArrayList<>();
	public static ArrayList<String> ANTIREC_WHITELIST = new ArrayList<>();
	public static ArrayList<String> AFK_ALLOWED = new ArrayList<>();
	public static ArrayList<String> LOGINABLE = new ArrayList<>();
	public static ArrayList<String> NOTIFY = new ArrayList<>();
}
