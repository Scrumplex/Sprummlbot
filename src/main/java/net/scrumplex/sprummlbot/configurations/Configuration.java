package net.scrumplex.sprummlbot.configurations;

import com.github.theholywaffle.teamspeak3.TS3Query;
import net.scrumplex.sprummlbot.Vars;
import net.scrumplex.sprummlbot.plugins.Config;
import net.scrumplex.sprummlbot.tools.EasyMethods;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Configuration {

    public static void load(File f, boolean silent) throws IOException, FontFormatException {
        if (!silent)
            System.out.println("Checking " + f.getName() + " if it is outdated...");
        Config conf = new Config(f).setDefaultConfig(getDefaultIni()).compare();
        if (conf.wasChanged()) {
            if (!silent)
                System.out.println(f.getName() + " was updated.");
        } else {
            if (!silent)
                System.out.println(f.getName() + " was up to date.");
        }
        final Ini ini = conf.getIni();

        Section connection = ini.get("Connection");
        Vars.SERVER = connection.get("ip");
        Vars.PORT_SQ = connection.get("port", int.class);
        Vars.CHANGE_FLOOD_SETTINGS = connection.get("optimize-flood-settings", boolean.class);

        Section login = ini.get("Login");
        Vars.LOGIN[0] = login.get("username");
        Vars.LOGIN[1] = login.get("password");
        Vars.SERVER_ID = login.get("server-id", int.class);

        Section webinterface = ini.get("Webinterface");
        Vars.WEBINTERFACE_PORT = webinterface.get("port", int.class);
        Vars.PERMGROUPASSIGNMENTS.put("command_login", webinterface.get("group"));

        Section appearance = ini.get("Appearance");
        Vars.NICK = appearance.get("nickname");
        Vars.PERMGROUPASSIGNMENTS.put("notify", appearance.get("notify-group"));

        Section afkmover = ini.get("AFK Mover");
        Vars.AFK_ENABLED = afkmover.get("enabled", boolean.class);
        Vars.AFK_CHANNEL_ID = afkmover.get("channelid", int.class);
        Vars.PERMGROUPASSIGNMENTS.put("afk", afkmover.get("whitelist-group"));
        if (afkmover.get("condition-away", boolean.class))
            Vars.AFK_CONDITIONS.add("away");
        if (afkmover.get("condition-mic-muted", boolean.class))
            Vars.AFK_CONDITIONS.add("mic-muted");
        if (afkmover.get("condition-mic-disabled", boolean.class))
            Vars.AFK_CONDITIONS.add("mic-disabled");
        if (afkmover.get("condition-speaker-muted", boolean.class))
            Vars.AFK_CONDITIONS.add("speaker-muted");
        if (afkmover.get("condition-speaker-disabled", boolean.class))
            Vars.AFK_CONDITIONS.add("speaker-disabled");
        Vars.AFK_CONDITIONS_MIN = afkmover.get("condition-min", int.class);
        Vars.AFK_TIME = afkmover.get("maxafktime", int.class) * 1000;
        List<String> bl = afkmover.getAll("afk-move-back-blacklist");
        for (String ids : bl)
            if (ids.contains(",")) {
                for (String id : ids.split(",")) {
                    if (EasyMethods.isInteger(id))
                        Vars.AFKMOVEBL.add(Integer.parseInt(id));
                    else
                        System.err.println(id + " in config.ini under \"AFK Mover\"->\"afk-move-back-blacklist\" is not valid and will be ignored!");
                }
            } else {
                if (EasyMethods.isInteger(ids))
                    Vars.AFKMOVEBL.add(Integer.parseInt(ids));
                else
                    System.err.println(ids + " in config.ini under \"AFK Mover\"->\"afk-move-back-blacklist\" is not valid and will be ignored!");
            }


        List<String> dontMove = afkmover.getAll("afk-allowed-channel-id");
        for (String ids : dontMove)
            if (ids.contains(","))
                for (String id : ids.split(","))
                    Vars.AFKALLOWED.add(Integer.parseInt(id));
            else
                Vars.AFKALLOWED.add(Integer.parseInt(ids));

        Section supportreminder = ini.get("Support Notifier");
        Vars.SUPPORT_ENABLED = supportreminder.get("enabled", boolean.class);
        Vars.SUPPORT_POKE = supportreminder.get("poke", boolean.class);
        Vars.PERMGROUPASSIGNMENTS.put("supporters", supportreminder.get("group"));
        List<String> sc = supportreminder.getAll("channelid");
        for (String ids : sc)
            if (ids.contains(","))
                for (String id : ids.split(","))
                    Vars.SUPPORT_CHANNEL_IDS.add(Integer.parseInt(id));
            else
                Vars.SUPPORT_CHANNEL_IDS.add(Integer.parseInt(ids));

        Section antirec = ini.get("Anti Recording");
        Vars.ANTIREC_ENABLED = antirec.get("enabled", boolean.class);
        Vars.PERMGROUPASSIGNMENTS.put("antirec", antirec.get("whitelist-group"));

        Section protector = ini.get("Server Group Protector");
        Vars.GROUPPROTECT_ENABLED = protector.get("enabled", boolean.class);

        Section broadcasts = ini.get("Broadcasts");
        Vars.BROADCAST_ENABLED = broadcasts.get("enabled", boolean.class);
        Vars.PERMGROUPASSIGNMENTS.put("broadcast", broadcasts.get("ignore-group"));
        Vars.BROADCAST_INTERVAL = broadcasts.get("interval", int.class);

        Section channelStats = ini.get("Channel Stats");
        Vars.CHANNELSTATS_ENABLED = channelStats.get("enabled", boolean.class);

        Section vpnChecker = ini.get("VPN Checker");
        Vars.VPNCHECKER_ENABLED = vpnChecker.get("enabled", boolean.class);
        Vars.VPNCHECKER_INTERVAL = vpnChecker.get("interval", int.class);
        Vars.VPNCHECKER_SAVE = vpnChecker.get("save-ips", boolean.class);
        Vars.PERMGROUPASSIGNMENTS.put("vpn", vpnChecker.get("whitelist-group"));

        Section logger = ini.get("Server Logger");
        Vars.LOGGER_ENABLED = logger.get("enabled", boolean.class);

        Section banner = ini.get("Dynamic Banner");
        Vars.DYNBANNER_ENABLED = banner.get("enabled", boolean.class);
        Vars.DYNBANNER_FILE = new File(banner.get("file"));
        GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ArrayList<String> fonts = new ArrayList<>(Arrays.asList(g.getAvailableFontFamilyNames()));

        if (fonts.contains(banner.get("font")))
            Vars.DYNBANNER_FONT = new Font(banner.get("font"), Font.PLAIN, banner.get("font-size", int.class));
        else {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(banner.get("font")));
            g.registerFont(font);
            Vars.DYNBANNER_FONT = new Font(font.getFontName(), Font.LAYOUT_LEFT_TO_RIGHT, banner.get("font-size", int.class));
        }
        Vars.DYNBANNER_COLOR = new Color(banner.get("color", int.class));
        Vars.DYNBANNER_TIME_POS[0] = banner.get("position-of-time-x", int.class);
        Vars.DYNBANNER_TIME_POS[1] = banner.get("position-of-time-y", int.class);
        Vars.DYNBANNER_DATE_POS[0] = banner.get("position-of-date-x", int.class);
        Vars.DYNBANNER_DATE_POS[1] = banner.get("position-of-date-y", int.class);
        Vars.DYNBANNER_USERS_POS[0] = banner.get("position-of-users-x", int.class);
        Vars.DYNBANNER_USERS_POS[1] = banner.get("position-of-users-y", int.class);

        Section misc = ini.get("Misc");

        Messages.setupLanguage(misc.get("language"), silent);
        Vars.UPDATE_ENABLED = misc.get("update-notification", boolean.class);
        Vars.TIMER_TICK = misc.get("check-tick", int.class);
        Vars.FLOODRATE = (misc.get("can-flood", boolean.class)) ? TS3Query.FloodRate.UNLIMITED : TS3Query.FloodRate.DEFAULT;
        Vars.DEBUG = misc.get("debug", int.class);
        Vars.IP = misc.get("ip");

        Section messages = ini.get("Messages");
        Vars.WELCOME_MSG = messages.get("welcome-msg-activated", boolean.class);
        Messages.add("skype", messages.get("skype-id"));
        Messages.add("website", messages.get("website"));
        Messages.add("youtube", messages.get("youtube"));


        Section commands = ini.get("Commands");
        List<String> disabled = commands.getAll("disabled");
        for (String ids : disabled)
            if (ids.contains(","))
                Collections.addAll(Vars.DISABLED_CONF_COMMANDS, ids.split(","));
            else
                Vars.DISABLED_CONF_COMMANDS.add(ids);
        if(!Vars.SUPPORT_ENABLED)
            Vars.DISABLED_CONF_COMMANDS.add("!support");
        if(!Vars.BROADCAST_ENABLED)
            Vars.DISABLED_CONF_COMMANDS.add("!mute");
        Vars.PERMGROUPASSIGNMENTS.put("command_toggle", commands.get("toggle-command-group"));
        Vars.PERMGROUPASSIGNMENTS.put("command_sendmsg", commands.get("sendmsg-command-group"));


        Permissions.load(new File("permissions.ini"), silent);
        Broadcasts.load(new File("broadcasts.ini"), silent);
        ServerGroupProtector.load(new File("groupprotect.ini"), silent);
        ChannelStats.load(new File("channelstats.ini"), silent);
        System.out.println("Config loaded!");
    }

    private static Ini getDefaultIni() {
        Ini defaultIni = new Ini();
        defaultIni.add("Connection");
        defaultIni.putComment("Connection", "Your Sprummlbot need this to connect to your server.");
        defaultIni.add("Login");
        defaultIni.putComment("Login", "Here you need to define the server query login, otherwise Sprummlbot won't be able to do his job.");
        defaultIni.add("Webinterface");
        defaultIni.putComment("Webinterface", "Here you can define the Port for the web interface.");
        defaultIni.add("Appearance");
        defaultIni.putComment("Appearance", "Here you define the name and the notification permission group for your bot.");
        defaultIni.add("AFK Mover");
        defaultIni.putComment("AFK Mover", "Here you need to set up the AFK-Mover feature of your new bot");
        defaultIni.add("Support Notifier");
        defaultIni.putComment("Support Notifier", "If you have a support section in your TeamSpeak, then you can define it here and the Sprummlbot will assist you-");
        defaultIni.add("Anti Recording");
        defaultIni.putComment("Anti Recording", "With this feature you can prevent users from recording.");
        defaultIni.add("Broadcasts");
        defaultIni.putComment("Broadcasts", "This feature can be used to announce messages frequently.");
        defaultIni.add("Server Group Protector");
        defaultIni.putComment("Server Group Protector", "This feature uses a whitelist for server groups, so unauthorized people can not acquire important groups.");
        defaultIni.add("Server Logger");
        defaultIni.putComment("Server Logger", "With this feature you can keep an eye on your server. This will write everything in a log file.");
        defaultIni.add("VPN Checker");
        defaultIni.putComment("VPN Checker", "This feature kicks clients which are faking their ip with VPNs. This feature is not perfect so do not expect 100% detection.");
        defaultIni.add("Channel Stats");
        defaultIni.putComment("Channel Stats", "With this feature you can add channels to your server which are displaying useful infirmation to your users.");
        defaultIni.add("Dynamic Banner");
        defaultIni.putComment("Dynamic Banner", "This feature can be used to have a server banner which updates every minute. It contains a clock, the date and the online users.");
        defaultIni.add("Messages");
        defaultIni.putComment("Messages", "With this feature you can inform your users about social media and contact things.");
        defaultIni.add("Commands");
        defaultIni.putComment("Commands", "Here you can define disabled commands and some permission groups for administrative commands.");
        defaultIni.add("Misc");
        defaultIni.putComment("Misc", "Here are some advanced options.");

        Section sec = defaultIni.get("Connection");
        sec.put("ip", "localhost");
        sec.putComment("ip", "IP of the teamspeak3 server (NO SRV Records)");
        sec.put("port", "10011");
        sec.putComment("port", "Port of Query Login (Leave this normal if you dont know it)");
        sec.put("optimize-flood-settings", false);
        sec.putComment("optimize-flood-settings", "Change this to true if you want to set the flood settings to: FLOOD_COMMANDS: 60 FLOOD_TIME: 15 BAN_TIME: 600");

        sec = defaultIni.get("Login");
        sec.put("username", "serveradmin");
        sec.putComment("username", "Put the username of ysour server's serveradminquery account");
        sec.put("password", "pass");
        sec.putComment("password", "Put the password of your server's serveradminquery account");
        sec.put("server-id", 1);
        sec.putComment("server-id", "Put the serverid of your server here (On self hosted servers it is 1");

        sec = defaultIni.get("Webinterface");
        sec.put("port", 9911);
        sec.putComment("port", "Port for the Webinterface. 0=disabled");
        sec.put("group", "Admins");
        sec.putComment("group", "Set the name of the group, for the Web Interface");

        sec = defaultIni.get("Appearance");
        sec.put("nickname", "Sprummlbot");
        sec.putComment("nickname", "Nickname for the Bot");
        sec.put("notify-group", "Admins");
        sec.putComment("notify-group", "The group for the people who will get sprummlbot messages.");

        sec = defaultIni.get("AFK Mover");
        sec.put("enabled", true);
        sec.putComment("enabled", "Defines if it is enabled");
        sec.put("channelid", 0);
        sec.putComment("channelid", "Defines the channel ID of the AFK Channel, where AFKs will be moved to");
        sec.put("whitelist-group", "VIPs");
        sec.putComment("whitelist-group", "Set the name of the group, for the AFK Whitelist");
        sec.put("condition-away", true);
        sec.putComment("condition-away", "Enable this if you want this as a condition");
        sec.put("condition-mic-muted", true);
        sec.putComment("condition-mic-muted", "Enable this if you want this as a condition");
        sec.put("condition-mic-disabled", true);
        sec.putComment("condition-mic-disabled", "Enable this if you want this as a condition");
        sec.put("condition-speaker-muted", true);
        sec.putComment("condition-speaker-muted", "Enable this if you want this as a condition");
        sec.put("condition-speaker-disabled", true);
        sec.putComment("condition-speaker-disabled", "Enable this if you want this as a condition");
        sec.put("condition-min", 2);
        sec.putComment("condition-min", "This defines when a client will be moved (-1 means all conditions have to be met)");
        sec.put("maxafktime", 600);
        sec.putComment("maxafktime", "Defines how long someone can be afk, if he is muted. (in seconds 600=10min)");
        sec.put("afk-move-back-blacklist", "4,5,6");
        sec.putComment("afk-move-back-blacklist", "This defines channels where afks will be moved from but not back.");

        sec.put("afk-allowed-channel-id", "1,2,3");
        sec.putComment("afk-allowed-channel-id",
                "Put the channel ids of the channels where being afk is allowed. e.g. music channels or support queue. To expand the list add in a new line afk-allowed-channel-id=%CHANNELID%");

        sec = defaultIni.get("Support Notifier");
        sec.put("enabled", true);
        sec.putComment("enabled", "Defines if it is enabled");
        sec.put("channelid", "7,8");
        sec.putComment("channelid", "Defines the channel IDs of support channels. You can define multiple ones here!");
        sec.put("poke", false);
        sec.putComment("poke", "Set this to true if you want to poke instead of private messages.");
        sec.put("group", "Supporters");
        sec.putComment("group", "Set the name of the group, for the Supporters");

        sec = defaultIni.get("Anti Recording");
        sec.put("enabled", true);
        sec.putComment("enabled", "Defines if it is enabled");
        sec.put("whitelist-group", "VIPs");
        sec.putComment("whitelist-group", "Set the name of the group, for the VPN Whitelist");

        sec = defaultIni.get("Broadcasts");
        sec.put("enabled", false);
        sec.putComment("enabled", "This is the broadcast feature. You can add messages to the broadcasts.ini");
        sec.put("interval", 300);
        sec.putComment("interval",
                "This sets the interval when messages will be sent to users. (in seconds! 300=5min)");
        sec.put("ignore-group", "VIPs");
        sec.putComment("ignore-group", "Set the name of the group, to ignore broadcasts.");

        sec = defaultIni.get("Server Logger");
        sec.put("enabled", true);
        sec.putComment("enabled", "This defines if the Sprummlbot should log all server events into a text file.");

        sec = defaultIni.get("VPN Checker");
        sec.put("enabled", true);
        sec.putComment("enabled", "This is the VPN Checker feature. This will kick everyone who uses vpn.");
        sec.put("interval", 50);
        sec.putComment("interval",
                "This sets the interval, when vpns should be checked. (in seconds! 60=1min)");
        sec.put("save-ips", true);
        sec.putComment("save-ips", "If the checker found an vpn, it's ip will be saved in an seperated config file. This could be network efficient.");
        sec.put("whitelist-group", "VIPs");
        sec.putComment("whitelist-group", "Set the name of the group, for the VPN Whitelist");

        sec = defaultIni.get("Channel Stats");
        sec.put("enabled", false);
        sec.putComment("enabled", "Enable or disable this feature here");

        sec = defaultIni.get("Dynamic Banner");
        sec.put("enabled", false);
        sec.putComment("enabled", "Only enable if you know what this does.");

        sec.put("file", "banner.png");
        sec.putComment("file", "This is the path to the original banner. Should be in the same directory as the Sprummlbot.jar and should be readable.");

        sec.put("font-size", 15);
        sec.putComment("font-size", "This defines the font size in pixels for the texts.");

        sec.put("font", "Dialog");
        sec.putComment("font", "Define here the Font name OR Font path of the font you want to use");

        sec.put("color", "000");
        sec.putComment("color", "This is the RGB color. The first number is red, the second green and the last blue. Do not use #000000. Use 000000");

        sec.put("position-of-time-x", 10);
        sec.putComment("position-of-time-x", "This is the x position of the time text");
        sec.put("position-of-time-y", 0);
        sec.putComment("position-of-time-y", "This is the y position of the time text");

        sec.put("position-of-date-x", 10);
        sec.putComment("position-of-date-x", "This is the x position of the date text");
        sec.put("position-of-date-y", 20);
        sec.putComment("position-of-date-y", "This is the y position of the date text");

        sec.put("position-of-users-x", 10);
        sec.putComment("position-of-users-x", "This is the x position of the online users text");
        sec.put("position-of-users-y", 40);
        sec.putComment("position-of-users-y", "This is the y position of the online users text");

        sec = defaultIni.get("Server Group Protector");
        sec.put("enabled", false);
        sec.putComment("enabled",
                "Enables the Server Group Protector. This protects users from joining Server Groups. It will be defined in groupprotect.ini");

        sec = defaultIni.get("Messages");
        sec.put("welcome-msg-activated", true);
        sec.putComment("welcome-msg-activated", "Put this to false if you do not want welcome messages when connecting.");
        sec.put("skype-id", "skypeid");
        sec.putComment("skype-id", "Skype ID for !skype command");
        sec.put("website", "website");
        sec.putComment("website", "Website for !web command");
        sec.put("youtube", "youtube");
        sec.putComment("youtube", "Youtube channel link for !yt command");

        sec = defaultIni.get("Commands");
        sec.put("toggle-command-group", "Admins");
        sec.putComment("toggle-command-group", "Set the allowed group for the command !toggle. NOTE: THIS COMMAND CAN DISABLE THE MODULES!");
        sec.put("sendmsg-command-group", "Admins");
        sec.putComment("sendmsg-command-group", "Set the allowed group for the command !sendmsg.");
        sec.put("disabled", "!COMMAND1");
        sec.add("disabled", "!COMMAND2");
        sec.putComment("disabled",
                "This list can disable the following commands: !login, !mute, !skype, !support, !web, !yt. These commands will also be disabled automatically if their features are disabled (e.g. !mute will be disabled if Broadcasts are disabled)");


        sec = defaultIni.get("Misc");
        sec.put("language", "en_US");
        sec.putComment("language", "Language definition. Available languages: you can add more languages at the messages.ini defual languages: de_DE, en_US, pt_BR and it");
        sec.put("update-notification", true);
        sec.putComment("update-notification",
                "Defines if the bot should check for updates (Bot will only send a message to console if an update is available.");
        sec.put("check-tick", 2000);
        sec.putComment("check-tick",
                "Defines the interval when Sprummlbot will check for AFK, Support or Recorders. Define in milliseconds (1second = 1000milliseconds). If you have problems with the Network Performance put this higher");
        sec.put("ip", "auto");
        sec.putComment("ip",
                "This is needed for the Dynamic Banner and the Mobile Connect feature. Set this to auto if your webinterface port is opened. Otherwise set it to auto (this will get the public ip of the bot). (Domains and DynDNS allowed)");
        sec.put("can-flood", false);
        sec.putComment("can-flood", "Set this to true if your bot's ip is whitelisted! If not keep it on false");
        sec.put("debug", 0);
        sec.putComment("debug", "Developers only. xD");
        return defaultIni;
    }
}
