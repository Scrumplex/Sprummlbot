package net.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.TS3Query;
import net.scrumplex.sprummlbot.core.Clients;
import net.scrumplex.sprummlbot.core.SprummlbotThreadFactory;
import net.scrumplex.sprummlbot.wrapper.PermissionGroup;
import net.scrumplex.sprummlbot.wrapper.State;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Vars {
    public static final String AD_LINK = "https://sprum.ml";
    public static final String VERSION = "0.4.9";
    static final int BUILD_ID = 49;
    public static final String AUTHOR = "Scrumplex";
    static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2, new SprummlbotThreadFactory());
    public static final ScheduledExecutorService SERVICE = Executors.newScheduledThreadPool(7, new SprummlbotThreadFactory());
    public static final HashMap<String, String> AVAILABLE_LOGINS = new HashMap<>();
    public static final String[] LOGIN = {"", ""};
    public static final Map<String, String> PERMGROUPASSIGNMENTS = new HashMap<>();
    public static final Map<String, PermissionGroup> PERMGROUPS = new HashMap<>();
    public static String IP;
    public static TS3Query QUERY;
    public static TS3Query.FloodRate FLOODRATE;
    public static String SERVER;
    public static int SERVER_ID;
    public static String NICK;
    public static int PORT_SQ;
    public static int WEBINTERFACE_PORT;
    public static boolean DYNBANNER_ENABLED = false;
    public static File DYNBANNER_FILE = null;
    public static Color DYNBANNER_COLOR = null;
    public static Font DYNBANNER_FONT = Font.getFont(Font.SERIF);
    public static byte[] DYNBANNER_GEN = null;
    public static boolean UPDATE_ENABLED = true;
    public static boolean UPDATE_AVAILABLE = false;
    public static boolean LOGGER_ENABLED = true;
    public static int DEBUG = 0;
    public static int TIMER_TICK = 4000;
    public static boolean CHANGE_FLOOD_SETTINGS = false;
    public static State SPRUMMLBOT_STATUS = State.STARTING;
    static int RECONNECT_TIMES = -1;
    public static Clients clients;
    public static int[] DYNBANNER_TIME_POS = new int[2];
    public static String DYNBANNER_TIME_F;
    public static int[] DYNBANNER_DATE_POS = new int[2];
    public static String DYNBANNER_DATE_F;
    public static int[] DYNBANNER_USERS_POS = new int[2];
    public static String DYNBANNER_USERS_F;
    public static boolean VPNCHECKER_ENABLED;
    public static boolean VPNCHECKER_SAVE;
    public static int VPNCHECKER_INTERVAL;
}