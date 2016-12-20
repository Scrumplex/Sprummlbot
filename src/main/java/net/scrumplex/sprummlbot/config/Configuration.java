package net.scrumplex.sprummlbot.config;

import com.github.theholywaffle.teamspeak3.TS3Query;
import net.scrumplex.sprummlbot.Vars;
import net.scrumplex.sprummlbot.plugins.Config;
import net.scrumplex.sprummlbot.wrapper.PermissionGroup;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Configuration {

    public static void load(File f, boolean silent) throws Exception {
        Config conf = new Config(f).setDefaultConfig(getDefaultIni()).compare();
        if (conf.wasChanged() && !silent)
            System.out.println("[Config] " + f.getName() + " was updated.");
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
        if (Vars.WEBINTERFACE_PORT <= 0) {
            throw new Exception("Web Interface port cannot be 0 or negative!");
        }
        PermissionGroup.setPermissionGroupField("command_login", webinterface.get("group"));

        Section appearance = ini.get("Appearance");
        Vars.NICK = appearance.get("nickname");
        PermissionGroup.setPermissionGroupField("notify", appearance.get("notify-group"));

        Section vpnChecker = ini.get("VPN Checker");
        Vars.VPNCHECKER_ENABLED = vpnChecker.get("enabled", boolean.class);
        Vars.VPNCHECKER_SAVE = vpnChecker.get("save-ips", boolean.class);
        Vars.VPNCHECKER_INTERVAL = vpnChecker.get("interval", int.class);
        PermissionGroup.setPermissionGroupField("vpn", vpnChecker.get("whitelist-group"));

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
            Vars.DYNBANNER_FONT = new Font(font.getFontName(), Font.PLAIN, banner.get("font-size", int.class));
        }
        Vars.DYNBANNER_COLOR = new Color(banner.get("color", int.class));
        Vars.DYNBANNER_TIME_POS[0] = banner.get("position-of-time-x", int.class);
        Vars.DYNBANNER_TIME_POS[1] = banner.get("position-of-time-y", int.class);
        Vars.DYNBANNER_TIME_F = banner.get("time-format");
        Vars.DYNBANNER_DATE_POS[0] = banner.get("position-of-date-x", int.class);
        Vars.DYNBANNER_DATE_POS[1] = banner.get("position-of-date-y", int.class);
        Vars.DYNBANNER_DATE_F = banner.get("date-format");
        Vars.DYNBANNER_USERS_POS[0] = banner.get("position-of-users-x", int.class);
        Vars.DYNBANNER_USERS_POS[1] = banner.get("position-of-users-y", int.class);
        Vars.DYNBANNER_USERS_F = banner.get("users-text");

        Section misc = ini.get("Misc");

        Vars.UPDATE_ENABLED = misc.get("update-notification", boolean.class);
        Vars.TIMER_TICK = misc.get("check-tick", int.class);
        Vars.FLOODRATE = (misc.get("can-flood", boolean.class)) ? TS3Query.FloodRate.UNLIMITED : TS3Query.FloodRate.DEFAULT;
        Vars.DEBUG = misc.get("debug", int.class);
        Vars.IP = misc.get("ip");


        Section commands = ini.get("Commands");

        PermissionGroup.setPermissionGroupField("command_sendmsg", commands.get("sendmsg-command-group"));

        Messages.setupLanguage(misc.get("language"), silent);
        Permissions.load(new File("permissions.ini"), silent);
        System.out.println("[Config] Config loaded!");
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
        defaultIni.add("Server Logger");
        defaultIni.putComment("Server Logger", "With this feature you can keep an eye on your server. This will write everything in a log file.");
        defaultIni.add("VPN Checker");
        defaultIni.putComment("VPN Checker", "This feature kicks clients which are faking their ip with VPNs. This feature is not perfect so do not expect 100% detection.");
        defaultIni.add("Dynamic Banner");
        defaultIni.putComment("Dynamic Banner", "This feature can be used to have a server banner which updates every minute. It contains a clock, the date and the online users.");
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
        sec.put("time-format", "HH:mm");
        sec.putComment("time-format", "Use a Java-Time Format here. Some of them are described in the Oracle Docs https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html");

        sec.put("position-of-date-x", 10);
        sec.putComment("position-of-date-x", "This is the x position of the date text");
        sec.put("position-of-date-y", 20);
        sec.putComment("position-of-date-y", "This is the y position of the date text");
        sec.put("date-format", "dd.MM.yyyy");
        sec.putComment("date-format", "Use a Java-Time Format here. Some of them are described in the Oracle Docs https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html");

        sec.put("position-of-users-x", 10);
        sec.putComment("position-of-users-x", "This is the x position of the online users text");
        sec.put("position-of-users-y", 40);
        sec.putComment("position-of-users-y", "This is the y position of the online users text");
        sec.put("users-text", "%users%/%max% online");
        sec.putComment("users-text", "%users% and %max% are variables. You need them!");

        sec = defaultIni.get("Commands");
        sec.put("sendmsg-command-group", "Admins");
        sec.putComment("sendmsg-command-group", "Set the allowed group for the command !sendmsg.");

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
