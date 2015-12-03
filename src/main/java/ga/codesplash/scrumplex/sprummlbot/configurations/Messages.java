package ga.codesplash.scrumplex.sprummlbot.configurations;

import ga.codesplash.scrumplex.sprummlbot.Vars;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class
 */
public class Messages {

    private static Language lang = null;

    /**
     * Loads Broadcasts Config File
     *
     * @param language Language, which will be loaded
     * @throws Exception
     */
    public static void setupLanguage(Language language) throws Exception {
        System.out.println("Sprummlbot language is: " + language.getID());
        lang = language;
        File f = new File("messages-" + language.getID() + ".ini");
        if (!f.exists()) {
            if (!f.createNewFile()) {
                System.out.println("Could not create " + f.getName());
            }
        }
        Ini ini = new Ini(f);
        System.out.println("Updating Config File " + f.getName());
        updateCFG(ini);

        Section global = ini.get("Global");
        HashMap<String, String> map = new HashMap<>();
        for (String key : global.keySet()) {
            map.put(key, global.get(key));
        }

        if (!map.containsKey("you-were-moved-to-afk") || !map.containsKey("you-mustnt-record-here")
                || !map.containsKey("you-were-moved-back-from-afk")
                || !map.containsKey("you-joined-support-channel")
                || !map.containsKey("you-are-not-longer-in-support-queue")
                || !map.containsKey("someone-is-in-support") || !map.containsKey("unknown-command")
                || !map.containsKey("you-wont-be-notified")
                || !map.containsKey("you-will-be-notified")) {
            throw new ConfigException("Language File is not defined carefully!");
        }

        msg = map;

        if (lang == Language.DE) {
            msg.put("help-dialog",
                    "[URL=" + Vars.AD_LINK + "]Sprummlbot[/URL] v" + Vars.VERSION + " von " + Vars.AUTHOR + ".");
            msg.put("commandslist", "Verf�gbare Befehle: [B]");
            msg.put("webinterface-disalbed", "Das Webinterface ist nicht aktiviert");
            msg.put("webinterface-your-user", "Dein Benutzername ist: ");
            msg.put("webinterface-your-pw", "Dein Passwort ist: ");
            msg.put("webinterface-login-is-temp", "Dieses Login ist tempor�r!");
            msg.put("welcome", "Willkommen, ");
        } else if (lang == Language.EN) {
            msg.put("help-dialog",
                    "[URL=" + Vars.AD_LINK + "]Sprummlbot[/URL] v" + Vars.VERSION + " by " + Vars.AUTHOR + ".");
            msg.put("commandslist", "Commands: [B]");
            msg.put("webinterface-disalbed", "Webinterface is disabled!");
            msg.put("webinterface-your-user", "Your username is: ");
            msg.put("webinterface-your-pw", "Your password is: ");
            msg.put("webinterface-login-is-temp", "This login is temporary!");
            msg.put("welcome", "Welcome, ");
        }
    }

    private static void updateCFG(Ini ini) throws IOException {
        updateCFG(ini, lang);
    }

    public static void updateCFG(Ini ini, Language lang) throws IOException {
        boolean changed = false;
        Map<String, String> de = new HashMap<>();
        de.put("you-were-moved-to-afk", "Du wurdest in den AFK Channel gemoved!");
        de.put("you-were-moved-back-from-afk", "Du wurdest zur�ck gemoved!");
        de.put("you-mustnt-record-here", "[B]Du darfst nicht aufnehmen![/B]");
        de.put("you-joined-support-channel", "Alle Teammitglieder wurden kontaktiert!");
        de.put("you-are-not-longer-in-support-queue", "Du wurdest aus der Warteschlange entfernt!");
        de.put("someone-is-in-support", "[B]Jemand is im Support Channel[/B]");
        de.put("unknown-command", "Dieser Befehl ist nicht bekannt!");
        de.put("you-wont-be-notified",
                "Du wirst nun keine Broadcast Nachrichten mehr erhalten. Dies ist nicht permanent");
        de.put("you-will-be-notified", "Du wirst von nun an wieder Broadcast Nachrichten bekommen!");

        Map<String, String> en = new HashMap<>();
        en.put("you-were-moved-to-afk", "You were moved to AFK Channel!");
        en.put("you-were-moved-back-from-afk", "You were moved back!");
        en.put("you-mustnt-record-here", "[B]You must not record here![/B]");
        en.put("you-joined-support-channel", "The team got a message!");
        en.put("you-are-not-longer-in-support-queue", "You are not longer in the queue!");
        en.put("someone-is-in-support", "[B]Someone needs support[/B]");
        en.put("unknown-command", "Unknown Command!");
        en.put("you-wont-be-notified", "You won't get broadcast messages anymore. This is not permanent!");
        en.put("you-will-be-notified", "You will get broadcast messages.");
        if (!ini.containsKey("Global")) {
            ini.add("Global");
        }
        Section sec = ini.get("Global");
        if (lang == Language.DE) {
            for (String key : de.keySet()) {
                if (!sec.containsKey(key)) {
                    sec.put(key, de.get(key));
                    changed = true;
                }
            }
        } else {
            for (String key : en.keySet()) {
                if (!sec.containsKey(key)) {
                    sec.put(key, en.get(key));
                    changed = true;
                }
            }
        }
        if (changed) {
            System.out.println("Saving updated config...");
            ini.store();
            System.out.println("Done! Please setup the new Configuration Sections!");
        }
    }

    public static String get(String id) {
        return msg.get(id);
    }

    public static void add(String id, String msgs) {
        msg.put(id, msgs);
    }

    private static HashMap<String, String> msg = new HashMap<>();
}
