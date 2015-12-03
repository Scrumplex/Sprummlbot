package ga.codesplash.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Just an variable class
 * Only variables are defined here
 */
public class Vars {
    public static TS3Query QUERY = null;
    public static TS3Api API = null;

    public static final String AD_LINK = "https://github.com/Scrumplex/Sprummlbot";
    public static final String VERSION = "0.2.8C";
    public static final int BUILD_ID = 28;
    public static final String AUTHOR = "Scrumplex";
    public static final List<String> NOTIFY = new ArrayList<>();

    public static String SERVER = "";
    public static final String[] LOGIN = {"", ""};
    public static int SERVER_ID = 1;
    public static int QID;
    public static String NICK = "Sprummlbot";
    public static int PORT_SQ = 10011;

    public static int WEBINTERFACE_PORT = 9911;
    public static final List<String> LOGINABLE = new ArrayList<>();
    public static final HashMap<String, String> AVAILABLE_LOGINS = new HashMap<>();

    public static boolean AFK_ENABLED = true;
    public static final List<Integer> AFKALLOWED = new ArrayList<>();
    public static int AFK_CHANNEL_ID = 0;
    public static int AFK_TIME = 600000;
    public static final List<String> AFK_ALLOWED = new ArrayList<>();
    public static final Map<String, Integer> IN_AFK = new HashMap<>();

    public static boolean SUPPORT_ENABLED = true;
    public static int SUPPORT_CHANNEL_ID = 0;
    public static final List<String> SUPPORTERS = new ArrayList<>();
    public static final List<String> IN_SUPPORT = new ArrayList<>();

    public static boolean ANTIREC_ENABLED = true;
    public static final List<String> ANTIREC_WHITELIST = new ArrayList<>();

    public static boolean BROADCAST_ENABLED = true;
    public static final List<String> BROADCASTS = new ArrayList<>();
    public static final List<String> BROADCAST_IGNORE = new ArrayList<>();
    public static Integer BROADCAST_INTERVAL = 300;

    public static boolean GROUPPROTECT_ENABLED = true;
    public static final Map<Integer, List<String>> GROUPPROTECT_LIST = new HashMap<>();

    public static boolean UPDATE_ENABLED = true;
    public static boolean UPDATE_AVAILABLE = false;
    public static int DEBUG = 0;
    public static int TIMER_TICK = 4000;
}
