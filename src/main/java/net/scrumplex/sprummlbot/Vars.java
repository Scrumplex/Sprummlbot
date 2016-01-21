package net.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.TS3Query;
import net.scrumplex.sprummlbot.tools.PermissionGroup;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Just an variable class
 * Only variables are defined here
 */
public class Vars {
    public static final String AD_LINK = "https://github.com/Scrumplex/Sprummlbot";
    public static final String VERSION = "0.3.7";
    public static final int BUILD_ID = 37;
    public static final String AUTHOR = "Scrumplex";
    public static final HashMap<String, String> AVAILABLE_LOGINS = new HashMap<>();
    public static final List<Integer> AFKALLOWED = new ArrayList<>();
    public static final Map<String, Integer> IN_AFK = new HashMap<>();
    public static final List<String> IN_SUPPORT = new ArrayList<>();
    public static final List<String> BROADCASTS = new ArrayList<>();
    public static final Map<Integer, List<String>> GROUPPROTECT_LIST = new HashMap<>();
    public static TS3Query QUERY = null;
    public static TS3ApiAsync API = null;
    public static int RECONNECT_TIMES = -1;
    public static TS3Query.FloodRate FLOODRATE = TS3Query.FloodRate.DEFAULT;
    public static String SERVER = "";
    public static final String[] LOGIN = {"", ""};
    public static int SERVER_ID = 1;
    public static int QID;
    public static String NICK = "Sprummlbot";
    public static int PORT_SQ = 10011;

    public static int WEBINTERFACE_PORT = 9911;

    public static boolean AFK_ENABLED = true;
    public static int AFK_CHANNEL_ID = 0;
    public static int AFK_TIME = 600000;

    public static boolean SUPPORT_ENABLED = true;
    public static int SUPPORT_CHANNEL_ID = 0;

    public static boolean ANTIREC_ENABLED = true;

    public static boolean BROADCAST_ENABLED = true;
    public static Integer BROADCAST_INTERVAL = 300;

    public static boolean VPNCHECKER_ENABLED = true;
    public static int VPNCHECKER_INTERVAL = 20;
    public static boolean VPNCHECKER_SAVE = true;

    public static boolean GROUPPROTECT_ENABLED = true;

    public static boolean INTERACTIVEBANNER_ENABLED = false;
    public static File INTERACTIVEBANNER_FILE = null;
    public static Color INTERACTIVEBANNER_COLOR = null;
    public static int INTERACTIVEBANNER_FONT_SIZE = 15;
    public static final int[] INTERACTIVEBANNER_TIME_POS = {0, 0};
    public static final int[] INTERACTIVEBANNER_DATE_POS = {0, 0};
    public static final int[] INTERACTIVEBANNER_USERS_POS = {0, 0};

    public static final Map<String, String> PERMGROUPASSIGNMENTS = new HashMap<>();
    public static final Map<String, PermissionGroup> PERMGROUPS = new HashMap<>();

    public static boolean UPDATE_ENABLED = true;
    public static boolean UPDATE_AVAILABLE = false;
    public static int DEBUG = 0;
    public static int TIMER_TICK = 4000;
    public static boolean CHANGE_FLOOD_SETTINGS = false;

    public static State SPRUMMLBOT_STATUS = State.STARTING;
}
