package ga.codesplash.scrumplex.sprummlbot.configurations;

import com.github.theholywaffle.teamspeak3.TS3Query;
import ga.codesplash.scrumplex.sprummlbot.Commands;
import ga.codesplash.scrumplex.sprummlbot.Vars;
import ga.codesplash.scrumplex.sprummlbot.plugins.Config;
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
     * @throws IOException
     */
    public static void load(File f) throws IOException {
        System.out.println("Checking " + f.getName() + " if it is outdated...");
        Config conf = new Config(f).setDefaultConfig(getDefaultIni()).compare();
        if (conf.wasChanged()) {
            System.out.println(f.getName() + " was updated.");
        } else {
            System.out.println(f.getName() + " was up to date.");
        }
        final Ini ini = conf.getIni();

        Section connection = ini.get("Connection");
        Vars.SERVER = connection.get("ip");
        Vars.PORT_SQ = connection.get("port", int.class);

        Section login = ini.get("Login");

        Vars.LOGIN[0] = login.get("username");
        Vars.LOGIN[1] = login.get("password");
        Vars.SERVER_ID = login.get("server-id", int.class);

        Section webinterface = ini.get("Webinterface");

        Vars.WEBINTERFACE_PORT = webinterface.get("port", int.class);

        Section appearance = ini.get("Appearance");

        Vars.NICK = appearance.get("nickname");

        Section afkmover = ini.get("AFK Mover");

        Vars.AFK_ENABLED = afkmover.get("enabled", boolean.class);
        Vars.AFK_CHANNEL_ID = afkmover.get("channelid", int.class);
        Vars.AFK_TIME = afkmover.get("maxafktime", int.class) * 1000;
        int[] dontmove = afkmover.getAll("afk-allowed-channel-id", int[].class);
        for (int id : dontmove) {
            Vars.AFKALLOWED.add(id);
        }

        Section supportreminder = ini.get("Support Reminder");

        Vars.SUPPORT_ENABLED = supportreminder.get("enabled", boolean.class);
        Vars.SUPPORT_CHANNEL_ID = supportreminder.get("channelid", int.class);

        Section antirec = ini.get("Anti Recording");

        Vars.ANTIREC_ENABLED = antirec.get("enabled", boolean.class);

        Section protector = ini.get("Server Group Protector");
        Vars.GROUPPROTECT_ENABLED = protector.get("enabled", boolean.class);

        Section broadcasts = ini.get("Broadcasts");

        Vars.BROADCAST_ENABLED = broadcasts.get("enabled", boolean.class);
        Vars.BROADCAST_INTERVAL = broadcasts.get("interval", int.class);

        Section vpnChecker = ini.get("VPN Checker");
        Vars.VPNCHECKER_ENABLED = vpnChecker.get("enabled", boolean.class);
        Vars.VPNCHECKER_INTERVAL = vpnChecker.get("interval", int.class);
        Vars.VPNCHECKER_SAVE = vpnChecker.get("save-ips", boolean.class);

        Section banner = ini.get("Interactive Server Banner");
        Vars.INTERACTIVEBANNER_ENABLED = banner.get("enabled", boolean.class);
        Vars.INTERACTIVEBANNER_FILE = new File(banner.get("file"));
        Vars.INTERACTIVEBANNER_FONT_SIZE = banner.get("font-size", int.class);
        String[] color = banner.get("color").split(" ");
        Vars.INTERACTIVEBANNER_COLOR = new Color(Integer.valueOf(color[0]), Integer.valueOf(color[1]), Integer.valueOf(color[2]));
        Vars.INTERACTIVEBANNER_TIME_POS[0] = banner.get("position-of-time-x", int.class);
        Vars.INTERACTIVEBANNER_TIME_POS[1] = banner.get("position-of-time-y", int.class);
        Vars.INTERACTIVEBANNER_DATE_POS[0] = banner.get("position-of-date-x", int.class);
        Vars.INTERACTIVEBANNER_DATE_POS[1] = banner.get("position-of-date-y", int.class);
        Vars.INTERACTIVEBANNER_USERS_POS[0] = banner.get("position-of-users-x", int.class);
        Vars.INTERACTIVEBANNER_USERS_POS[1] = banner.get("position-of-users-y", int.class);

        Section misc = ini.get("Misc");

        if (Language.fromID(misc.get("language")) != null) {
            Messages.setupLanguage(Language.fromID(misc.get("language")));
        } else {
            System.out.println("You defined a not supported language in config! Setting to EN!");
            Messages.setupLanguage(Language.EN_US);
        }

        Vars.UPDATE_ENABLED = misc.get("update-notification", boolean.class);

        Vars.TIMER_TICK = misc.get("check-tick", int.class);

        Vars.FLOODRATE = (misc.get("can-flood", int.class) > 0) ? TS3Query.FloodRate.UNLIMITED : TS3Query.FloodRate.DEFAULT;

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

        if (Vars.DEBUG == 2) {
            for (String str : ini.keySet()) {
                for (String out : ini.get(str).keySet()) {
                    System.out.println("[DEBUG] [CONF] [config.ini] " + str + "." + out + ": " + ini.get(str).get(out));
                }
            }
        }

        Clients.load(new File("clients.ini"));
        Broadcasts.load(new File("broadcasts.ini"));
        ServerGroupProtector.load(new File("groupprotect.ini"));
        System.out.println("Config loaded!");

    }

    public static Ini getDefaultIni() {
        Ini defaultIni = new Ini();
        defaultIni.add("Connection");
        defaultIni.add("Login");
        defaultIni.add("Webinterface");
        defaultIni.add("Appearance");
        defaultIni.add("AFK Mover");
        defaultIni.add("Support Reminder");
        defaultIni.add("Anti Recording");
        defaultIni.add("Broadcasts");
        defaultIni.add("Server Group Protector");
        defaultIni.add("VPN Checker");
        defaultIni.add("Interactive Server Banner");
        defaultIni.add("Messages");
        defaultIni.add("Commands");
        defaultIni.add("Misc");

        Section sec = defaultIni.get("Connection");
        sec.put("ip", "localhost");
        sec.putComment("ip", "IP of the teamspeak3 server (NO SRV Records)");
        sec.put("port", "10011");
        sec.putComment("port", "Port of Query Login (Leave this normal if you dont know it)");

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

        sec = defaultIni.get("Appearance");
        sec.put("nickname", "Sprummlbot");
        sec.putComment("nickname", "Nickname for the Bot");

        sec = defaultIni.get("AFK Mover");
        sec.put("enabled", true);
        sec.putComment("enabled", "Defines if it is enabled");
        sec.put("channelid", 0);
        sec.putComment("channelid", "Defines the channel ID of the AFK Channel, where AFKs will be moved to");

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

        sec = defaultIni.get("Anti Recording");
        sec.put("enabled", true);
        sec.putComment("enabled", "Defines if it is enabled");

        sec = defaultIni.get("Broadcasts");
        sec.put("enabled", false);
        sec.putComment("enabled", "This is the broadcast feature. You can add messages to the broadcasts.ini");
        sec.put("interval", 300);
        sec.putComment("interval",
                "This sets the interval when messages will be sent to users. (in seconds! 300=5min)");

        sec = defaultIni.get("VPN Checker");
        sec.put("enabled", true);
        sec.putComment("enabled", "This is the VPN Checker feature. This will kick everyone who uses vpn.");
        sec.put("interval", 50);
        sec.putComment("interval",
                "This sets the interval, when vpns should be checked. (in seconds! 60=1min)");
        sec.put("save-ips", true);
        sec.putComment("save-ips", "If the checker found an vpn, it's ip will be saved in an seperated config file. This could be network efficient.");

        sec = defaultIni.get("Interactive Server Banner");
        sec.put("enabled", false);
        sec.putComment("enabled", "Only enable ifyou know waht this does.");

        sec.put("file", "banner.png");
        sec.putComment("file", "This is the path to the original banner. Shoudl be in the same directory as the Sprummlbot.jar and should be readable.");

        sec.put("font-size", 15);
        sec.putComment("font-size", "This defines the font size in pixels for the texts.");

        sec.put("color", "0 0 0");
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

        sec = defaultIni.get("Commands");
        sec.put("disabled", "!COMMAND1");
        sec.add("disabled", "!COMMAND2");
        sec.putComment("disabled",
                "This list can disable the following commands: !login, !mute, !skype, !support, !web, !yt. These commands will also be disabled automatically if their features are disabled (e.g. !mute will be disabled if Broadcasts are disabled)");

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

        sec = defaultIni.get("Misc");
        sec.put("language", "en_US");
        sec.putComment("language", "Language definition. Available languages: de_DE(German), en_US(English), pt_BR(Brazilian Portuguese)");
        sec.put("update-notification", true);
        sec.putComment("update-notification",
                "Defines if the bot should check for updates (Bot will only send a message to console if an update is available.");
        sec.put("check-tick", 4000);
        sec.putComment("check-tick",
                "Defines the interval when Sprummlbot will check for AFK, Support or Recorders. Define in milliseconds (1second = 1000milliseconds). If you have problems with the Network Performance put this higher");
        sec.put("can-flood", 0);
        sec.putComment("can-flood", "Set this to 1 if your bot's ip is whitelisted! If not keep it on 0");
        sec.put("debug", 0);
        sec.putComment("debug", "Developers only. xD");
        return defaultIni;
    }
}
