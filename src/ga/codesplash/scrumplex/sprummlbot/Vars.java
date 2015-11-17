package ga.codesplash.scrumplex.sprummlbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Query;

public class Vars {
	public static TS3Query QUERY = null;
	public static TS3Api API = null;

	public static String AD_LINK = "https://github.com/Scrumplex/Sprummlbot";
	public static String VERSION = "0.2.8";
	public static int BUILDID = 28;
	public static String AUTHOR = "Scrumplex";
	public static List<String> NOTIFY = new ArrayList<>();

	public static String SERVER = "";
	public static String[] LOGIN = { "", "" };
	public static int SERVERID = 1;
	public static int QID;
	public static String NICK = "Sprummlbot";
	public static int PORT_SQ = 10011;
	
	public static int WEBINTERFACE_PORT = 9911;
	public static List<String> LOGINABLE = new ArrayList<>();

	public static boolean AFK_ENABLED = true;
	public static List<Integer> AFKALLOWED = new ArrayList<>();
	public static int AFKCHANNELID = 0;
	public static int AFKTIME = 600000;
	public static List<String> AFK_ALLOWED = new ArrayList<>();
	public static Map<String, Integer> INAFK = new HashMap<>();

	public static boolean SUPPORT_ENABLED = true;
	public static int SUPPORTCHANNELID = 0;
	public static List<String> SUPPORTERS = new ArrayList<>();
	public static List<String> INSUPPORT = new ArrayList<>();

	public static boolean ANTIREC_ENABLED = true;
	public static List<String> ANTIREC_WHITELIST = new ArrayList<>();

	public static boolean BROADCAST_ENABLED = true;
	public static List<String> BROADCASTS = new ArrayList<>();
	public static List<String> BROADCAST_IGNORE = new ArrayList<>();
	public static Integer BROADCAST_INTERVAL = 300;

	public static boolean GROUPPROTECT_ENABLED = true;
	public static Map<Integer, List<String>> GROUPPROTECT_LIST = new HashMap<>();

	public static boolean UPDATE_ENABLED = true;
	public static boolean UPDATE_AVAILABLE = false;
	public static int DEBUG = 0;
	public static int TIMERTICK = 4000;
}
