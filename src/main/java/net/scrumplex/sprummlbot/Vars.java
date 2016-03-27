package net.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.TS3Query;
import net.scrumplex.sprummlbot.plugins.CommandManager;
import net.scrumplex.sprummlbot.wrapper.PermissionGroup;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Vars {
    public static final String AD_LINK = "https://sprum.ml";
    public static final String VERSION = "0.4.4";
    static final int BUILD_ID = 44;
    public static final String AUTHOR = "Scrumplex";
    public static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);
    //TODO
    static final boolean PRERELEASE = false;
    public static final HashMap<String, String> AVAILABLE_LOGINS = new HashMap<>();
    public static final List<Integer> AFKALLOWED = new ArrayList<>();
    public static final List<Integer> AFKMOVEBL = new ArrayList<>();
    static final Map<String, Integer> IN_AFK = new HashMap<>();
    static final List<String> IN_SUPPORT = new ArrayList<>();
    public static final List<String> BROADCASTS = new ArrayList<>();
    public static final Map<Integer, List<String>> GROUPPROTECT_LIST = new HashMap<>();
    public static final String[] LOGIN = {"", ""};
    public static final int[] DYNBANNER_TIME_POS = {0, 0};
    public static final int[] DYNBANNER_DATE_POS = {0, 0};
    public static final int[] DYNBANNER_USERS_POS = {0, 0};
    public static final Map<String, String> PERMGROUPASSIGNMENTS = new HashMap<>();
    public static final Map<String, PermissionGroup> PERMGROUPS = new HashMap<>();
    public static final Map<Integer, String> CHANNELSTATS = new HashMap<>();
    public static boolean CHANNELSTATS_ENABLED = false;
    public static boolean WELCOME_MSG = true;
    public static String IP = "";
    public static TS3Query QUERY = null;
    public static TS3ApiAsync API = null;
    static int RECONNECT_TIMES = -1;
    public static TS3Query.FloodRate FLOODRATE = TS3Query.FloodRate.DEFAULT;
    public static String SERVER = "";
    public static int SERVER_ID = 1;
    static int QID;
    public static String NICK = "Sprummlbot";
    public static int PORT_SQ = 10011;
    public static int WEBINTERFACE_PORT = 9911;
    public static boolean AFK_ENABLED = true;
    public static int AFK_CHANNEL_ID = 0;
    public static int AFK_TIME = 600000;
    public static final List<String> AFK_CONDITIONS = new ArrayList<>();
    public static int AFK_CONDITIONS_MIN = -1;
    public static boolean SUPPORT_ENABLED = true;
    public static boolean SUPPORT_POKE = false;
    public static final List<Integer> SUPPORT_CHANNEL_IDS = new ArrayList<>();
    public static boolean ANTIREC_ENABLED = true;
    public static boolean BROADCAST_ENABLED = true;
    public static int BROADCAST_INTERVAL = 300;
    public static boolean VPNCHECKER_ENABLED = true;
    public static int VPNCHECKER_INTERVAL = 20;
    public static boolean VPNCHECKER_SAVE = true;
    public static boolean GROUPPROTECT_ENABLED = true;
    public static boolean DYNBANNER_ENABLED = false;
    public static File DYNBANNER_FILE = null;
    public static Color DYNBANNER_COLOR = null;
    public static Font DYNBANNER_FONT = Font.getFont(Font.SERIF);
    public static boolean UPDATE_ENABLED = true;
    public static boolean UPDATE_AVAILABLE = false;
    public static boolean LOGGER_ENABLED = true;

    public static CommandManager COMMAND_MGR = null;
    public static final List<String> DISABLED_CONF_COMMANDS = new ArrayList<>();

    public static int DEBUG = 0;
    public static int TIMER_TICK = 4000;
    public static boolean CHANGE_FLOOD_SETTINGS = false;

    static State SPRUMMLBOT_STATUS = State.STARTING;
}