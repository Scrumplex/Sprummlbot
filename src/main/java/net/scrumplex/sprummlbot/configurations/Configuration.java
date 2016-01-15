package net.scrumplex.sprummlbot.configurations;

import com.github.theholywaffle.teamspeak3.TS3Query;
import net.scrumplex.sprummlbot.Commands;
import net.scrumplex.sprummlbot.Vars;
import net.scrumplex.sprummlbot.plugins.Config;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Configuration class
 */
public class Configuration {

    /**
     * Loads Config File
     *
     * @param f File, which will be loaded
     * @param silent If there should be console output or not
     * @throws IOException
     */
    public static void load(File f, boolean silent) throws IOException {
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
        Vars.PERMGROUPASSIGNMENTS.put("webinterface", webinterface.get("group"));

        Section appearance = ini.get("Appearance");
        Vars.NICK = appearance.get("nickname");
        Vars.PERMGROUPASSIGNMENTS.put("notify", appearance.get("notify-group"));

        Section afkmover = ini.get("AFK Mover");
        Vars.AFK_ENABLED = afkmover.get("enabled", boolean.class);
        Vars.AFK_CHANNEL_ID = afkmover.get("channelid", int.class);
        Vars.PERMGROUPASSIGNMENTS.put("afk", afkmover.get("whitelist-group"));
        Vars.AFK_TIME = afkmover.get("maxafktime", int.class) * 1000;
        int[] dontmove = afkmover.getAll("afk-allowed-channel-id", int[].class);
        for (int id : dontmove) {
            Vars.AFKALLOWED.add(id);
        }

        Section supportreminder = ini.get("Support Reminder");
        Vars.SUPPORT_ENABLED = supportreminder.get("enabled", boolean.class);
        Vars.PERMGROUPASSIGNMENTS.put("supporters", supportreminder.get("group"));
        Vars.SUPPORT_CHANNEL_ID = supportreminder.get("channelid", int.class);

        Section antirec = ini.get("Anti Recording");
        Vars.ANTIREC_ENABLED = antirec.get("enabled", boolean.class);
        Vars.PERMGROUPASSIGNMENTS.put("antirec", antirec.get("whitelist-group"));

        Section protector = ini.get("Server Group Protector");
        Vars.GROUPPROTECT_ENABLED = protector.get("enabled", boolean.class);

        Section broadcasts = ini.get("Broadcasts");
        Vars.BROADCAST_ENABLED = broadcasts.get("enabled", boolean.class);
        Vars.PERMGROUPASSIGNMENTS.put("broadcast", broadcasts.get("ignore-group"));
        Vars.BROADCAST_INTERVAL = broadcasts.get("interval", int.class);

        Section vpnChecker = ini.get("VPN Checker");
        Vars.VPNCHECKER_ENABLED = vpnChecker.get("enabled", boolean.class);
        Vars.VPNCHECKER_INTERVAL = vpnChecker.get("interval", int.class);
        Vars.VPNCHECKER_SAVE = vpnChecker.get("save-ips", boolean.class);
        Vars.PERMGROUPASSIGNMENTS.put("vpn", afkmover.get("whitelist-group"));

        Section banner = ini.get("Interactive Server Banner");
        Vars.INTERACTIVEBANNER_ENABLED = banner.get("enabled", boolean.class);
        Vars.INTERACTIVEBANNER_FILE = new File(banner.get("file"));
        Vars.INTERACTIVEBANNER_FONT_SIZE = banner.get("font-size", int.class);
        Vars.INTERACTIVEBANNER_COLOR = new Color(banner.get("color", int.class));
        Vars.INTERACTIVEBANNER_TIME_POS[0] = banner.get("position-of-time-x", int.class);
        Vars.INTERACTIVEBANNER_TIME_POS[1] = banner.get("position-of-time-y", int.class);
        Vars.INTERACTIVEBANNER_DATE_POS[0] = banner.get("position-of-date-x", int.class);
        Vars.INTERACTIVEBANNER_DATE_POS[1] = banner.get("position-of-date-y", int.class);
        Vars.INTERACTIVEBANNER_USERS_POS[0] = banner.get("position-of-users-x", int.class);
        Vars.INTERACTIVEBANNER_USERS_POS[1] = banner.get("position-of-users-y", int.class);

        Section misc = ini.get("Misc");

        Messages.setupLanguage(misc.get("language"), silent);
        Vars.UPDATE_ENABLED = misc.get("update-notification", boolean.class);
        Vars.TIMER_TICK = misc.get("check-tick", int.class);
        Vars.FLOODRATE = (misc.get("can-flood", boolean.class)) ? TS3Query.FloodRate.UNLIMITED : TS3Query.FloodRate.DEFAULT;
        Vars.DEBUG = misc.get("debug", int.class);

        Section messages = ini.get("Messages");
        Messages.add("skype", messages.get("skype-id"));
        Messages.add("website", messages.get("website"));
        Messages.add("youtube", messages.get("youtube"));
        Vars.AFKALLOWED.add(Vars.AFK_CHANNEL_ID);
        Vars.AFKALLOWED.add(Vars.SUPPORT_CHANNEL_ID);

        Section commands = ini.get("Commands");
        if (commands.containsKey("disabled")) {
            Commands.setup(commands.getAll("disabled", String[].class));
        } else {
            Commands.setup(new String[0]);
        }

        Permissions.load(new File("permissions.ini"), silent);
        Broadcasts.load(new File("broadcasts.ini"), silent);
        ServerGroupProtector.load(new File("groupprotect.ini"), silent);
        System.out.println("Config loaded!");

    }

    public static Ini getDefaultIni() {
        Ini defaultIni = new Ini();
        defaultIni.add("Connection");
        defaultIni.putComment("Connection", "Your Sprummlbot need this to connect to your Server.");
        defaultIni.add("Login");
        defaultIni.putComment("Login", "Here you need to define the Query Login. Otherwise Sprummlbot won't be able to do his job. :(");
        defaultIni.add("Webinterface");
        defaultIni.putComment("Webinterface", "Here you define the Port for the Webinterface.");
        defaultIni.add("Appearance");
        defaultIni.putComment("Appearance", "Here you define the Name for your new Sprummlbot :)");
        defaultIni.add("AFK Mover");
        defaultIni.putComment("AFK Mover", "Here you need to set up the AFK-Mover Feature of your new Bot :)");
        defaultIni.add("Support Reminder");
        defaultIni.putComment("Support Reminder", "If you have a Support Section in your Teamspeak, then you can define it here and the Sprummlbot will assist you :)");
        defaultIni.add("Anti Recording");
        defaultIni.putComment("Anti Recording", "You want to prevent recording on your Teamspeak? Define it here, and the Sprummlbot kicks everyone who tries it. :)");
        defaultIni.add("Broadcasts");
        defaultIni.putComment("Broadcasts", "You wan't to announce Messages to other users frequently? Then define it here. :)");
        defaultIni.add("Server Group Protector");
        defaultIni.putComment("Server Group Protector", "Your Server has Groups who others should not get? Then you can activate a Group-Protector for specific Groups here :)");
        defaultIni.add("VPN Checker");
        defaultIni.putComment("VPN Checker", "You hate VPN-Users on your Servers? Then Activate the VPN-Checker. Sprummlbot will kick everyone who connects/is connected with a VPN.");
        defaultIni.add("Interactive Server Banner");
        defaultIni.putComment("Interactive Server Banner", "You wan't an Interactive Banner for your Teamspeak? (With Time, Date, Slots), Then define it here. :)");
        defaultIni.add("Messages");
        defaultIni.putComment("Messages", "You wan't to define Custom Messages for your Sprummlbot. Then you're right here! :)");
        defaultIni.add("Commands");
        defaultIni.putComment("Commands", "Some commands should not be used by others? Then set it up here :)");
        defaultIni.add("Misc");
        defaultIni.putComment("Misc", "Here are some other Options which you can set-up for your Sprummlbot :)");

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
        sec.put("maxafktime", 600);
        sec.putComment("maxafktime", "Defines how long someone can be afk, if he is muted. (in seconds 600=10min)");
        sec.put("afk-allowed-channel-id", 1);
        sec.add("afk-allowed-channel-id", 2);
        sec.add("afk-allowed-channel-id", 3);
        sec.putComment("afk-allowed-channel-id",
                "Put the channel ids of the channels where being afk is allowed. e.g. music channels or support queue. To expand the list add in a new line afk-allowed-channel-id=%CHANNELID%");

        sec = defaultIni.get("Support Reminder");
        sec.put("enabled", true);
        sec.putComment("enabled", "Defines if it is enabled");
        sec.put("channelid", 0);
        sec.putComment("channelid", "Defines the channel ID of a support queue channel");
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

        sec = defaultIni.get("Interactive Server Banner");
        sec.put("enabled", false);
        sec.putComment("enabled", "Only enable if you know waht this does.");

        sec.put("file", "banner.png");
        sec.putComment("file", "This is the path to the original banner. Should be in the same directory as the Sprummlbot.jar and should be readable.");

        sec.put("font-size", 15);
        sec.putComment("font-size", "This defines the font size in pixels for the texts.");

        sec.put("color", "000");
        sec.putComment("color", "This is the RGB color. The first number is red, the second green and the last blue. Don't use #000000 or something like that. You can use http://html-color-codes.info/ and copy the r g and b values");

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
        sec.put("skype-id", "skypeid");
        sec.putComment("skype-id", "Skype ID for !skype command");
        sec.put("website", "website");
        sec.putComment("website", "Website for !web command");
        sec.put("youtube", "youtube");
        sec.putComment("youtube", "Youtube channel link for !yt command");

        sec = defaultIni.get("Commands");
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
        sec.put("check-tick", 4000);
        sec.putComment("check-tick",
                "Defines the interval when Sprummlbot will check for AFK, Support or Recorders. Define in milliseconds (1second = 1000milliseconds). If you have problems with the Network Performance put this higher");
        sec.put("can-flood", false);
        sec.putComment("can-flood", "Set this to true if your bot's ip is whitelisted! If not keep it on false");
        sec.put("debug", 0);
        sec.putComment("debug", "Developers only. xD");
        return defaultIni;
    }
}
