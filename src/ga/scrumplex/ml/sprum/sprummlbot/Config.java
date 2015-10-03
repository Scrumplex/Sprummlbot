package ga.scrumplex.ml.sprum.sprummlbot;

import java.util.ArrayList;
import java.util.HashMap;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

public class Config {
	public static TS3Query QUERY = null;	
	public static TS3Api API = null;	
	public static HashMap<String, Integer> INAFK = new HashMap<String, Integer>();
	public static ArrayList<String> INSUPPORT = new ArrayList<String>();
	
	public static String VERSION = "0.1.4";
	public static int BUILDID = 14;
	public static String AUTHOR = "Scrumplex";
	
	public static String SERVER = "";
	public static String[] LOGIN = {"", ""};
	public static ArrayList<String> TEAM = new ArrayList<String>();
	public static int SERVERID = 1;
	public static int QID;
	public static String NICK = "Sprummlbot";
	public static int PORT_WI = 9911;
	public static int PORT_SQ = 10011;
	
	public static boolean AFK_ENABLED = true;
	public static ArrayList<Integer> AFKALLOWED = new ArrayList<>();
	public static int AFKCHANNELID = 0;
	public static int AFKTIME = 600*1000;
	public static boolean AFK_MOVE_TEAM = false;
	
	public static boolean SUPPORT_ENABLED = true;
	public static int SUPPORTCHANNELID = 0;
	
	public static boolean ANTIREC_ENABLED = true;
	public static boolean ANTIREC_IGNORE_TEAM = true;
	
	
	public static boolean UPDATER_ENABLED = true;
	public static int DEBUG = 0;
	public static int TIMERTICK = 4000;
	
	public static Client getClientbyID(int id) {
		return API.getClientInfo(id);
	}
}
