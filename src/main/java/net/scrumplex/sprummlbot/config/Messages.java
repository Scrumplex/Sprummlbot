package net.scrumplex.sprummlbot.config;

import net.scrumplex.sprummlbot.Vars;
import net.scrumplex.sprummlbot.plugins.Config;
import net.scrumplex.sprummlbot.tools.Exceptions;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Messages {
    private static final HashMap<String, String> msg = new HashMap<>();

    static void setupLanguage(String id, boolean silent) throws IOException {
        File f = new File("messages.ini");
        Config conf = new Config(f).setDefaultConfig(getDefaultIni()).compare();
        if (conf.wasChanged() && !silent)
            System.out.println("[Config] " + f.getName() + " has been updated.");
        final Ini ini = conf.getIni();

        if (!ini.containsKey(id))
            Exceptions.handle(new Exception("Language code " + id + " is not valid!"), "Language code " + id + " is not valid!", true);
        Section section = ini.get(id);
        for (String key : section.keySet()) {
            msg.put(key, section.get(key));
        }
        msg.put("help-dialog",
                "[URL=https://sprum.ml]Sprummlbot[/URL] v" + Vars.VERSION + " by Scrumplex.");
    }

    private static Ini getDefaultIni() {
        Ini defaultIni = new Ini();
        Section tempSection = defaultIni.add("de_DE");
        tempSection.put("commandslist", "Verfügbare Befehle: [B]%commands%");
        tempSection.put("webinterface-your-user", "Dein Benutzername ist: %wi-username%");
        tempSection.put("webinterface-your-pw", "Dein Passwort ist: %wi-password%");
        tempSection.put("webinterface-login-is-temp", "Dieses Login ist temporär!");
        tempSection.put("welcome", "Willkommen, %client-username%");
        tempSection.put("you-were-moved-to-afk", "Du wurdest in den AFK Channel verschoben.");
        tempSection.put("you-were-moved-back-from-afk", "Du wurdest zurück verschoben!");
        tempSection.put("you-mustnt-record-here", "Du darfst auf diesem Server nicht aufnehmen!");
        tempSection.put("you-joined-notify-channel", "Die Team-Mitglieder wurden kontaktiert!");
        tempSection.put("unknown-command", "Dieser Befehl ist nicht bekannt!");
        tempSection.put("command-error", "Dieser Befehl konnte nicht verarbeitet werden!");
        tempSection.put("command-no-permission", "Du benötigst höhere Rechte, um diesen Befehl auszuführen!");
        tempSection.put("command-syntax-err", "Verwendung: %commandsyntax%");
        tempSection.put("you-are-using-vpn", "Du benutzt einen VPN Dienst. Bitte deaktiviere diesen.");

        tempSection = defaultIni.add("en_US");
        tempSection.put("commandslist", "Commands: [B]%commands%");
        tempSection.put("webinterface-your-user", "Your username is: %wi-username%");
        tempSection.put("webinterface-your-pw", "Your password is: %wi-password%");
        tempSection.put("webinterface-login-is-temp", "This login is temporary!");
        tempSection.put("welcome", "Welcome, %client-username%");
        tempSection.put("you-were-moved-to-afk", "You have been moved to the AFK channel.");
        tempSection.put("you-were-moved-back-from-afk", "You have been moved back.");
        tempSection.put("you-mustnt-record-here", "You are not allowed to capture sound!");
        tempSection.put("you-joined-notify-channel", "The team has been contacted.");
        tempSection.put("unknown-command", "Unknown Command.");
        tempSection.put("command-error", "An error occurred while processing the command!");
        tempSection.put("command-no-permission", "You are not allowed to use this command!");
        tempSection.put("command-syntax-err", "Usage: %commandsyntax%");
        tempSection.put("you-are-using-vpn", "You are using a VPN Service. Please disable it!");
        return defaultIni;
    }

    public static String get(String id) {
        return msg.get(id);
    }

    public static void add(String id, String msgs) {
        msg.put(id, msgs);
    }
}
