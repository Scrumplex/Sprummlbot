package ga.codesplash.scrumplex.sprummlbot.configurations;

import ga.codesplash.scrumplex.sprummlbot.Vars;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Configuration class
 */
public class Messages {

    private static Language lang = null;
    private static HashMap<String, String> msg = new HashMap<>();

    /**
     * Loads Broadcasts Config File
     *
     * @param language Language, which will be loaded
     * @throws Exception
     */
    public static void setupLanguage(Language language) throws Exception {
        System.out.println("Sprummlbot language is: " + language.getID());
        lang = language;
        File f = new File("messages.ini");
        Ini ini = updateCFG(f);

        Section section = ini.get(language.getID());
        for (String key : section.keySet()) {
            msg.put(key, section.get(key));
        }
        msg.put("help-dialog",
                "[URL=" + Vars.AD_LINK + "]Sprummlbot[/URL] v" + Vars.VERSION + " by " + Vars.AUTHOR + ".");
    }

    public static Ini updateCFG(File configFile) throws IOException {
        System.out.println("Updating Config File " + configFile.getName());
        boolean changed = false;
        if (!configFile.exists()) {
            if (!configFile.createNewFile()) {
                System.out.println("Could not create " + configFile.getName());
            }
        }
        Ini ini = new Ini(configFile);
        Ini defaultIni = new Ini();
        Section de_DE = defaultIni.add(Language.DE_DE.getID());
        de_DE.put("commandslist", "Verfügbare Befehle: [B]%commands%");
        de_DE.put("webinterface-disalbed", "Das Webinterface ist nicht aktiviert");
        de_DE.put("webinterface-your-user", "Dein Benutzername ist: %wi-username%");
        de_DE.put("webinterface-your-pw", "Dein Passwort ist: %wi-password%");
        de_DE.put("webinterface-login-is-temp", "Dieses Login ist temporär!");
        de_DE.put("welcome", "Willkommen, %client-username%");
        de_DE.put("you-were-moved-to-afk", "Du wurdest in den AFK Channel gemovet!");
        de_DE.put("you-were-moved-back-from-afk", "Du wurdest zurück gemoved!");
        de_DE.put("you-mustnt-record-here", "[B]Du darfst nicht aufnehmen![/B]");
        de_DE.put("you-joined-support-channel", "Alle Teammitglieder wurden kontaktiert!");
        de_DE.put("you-are-not-longer-in-support-queue", "Du wurdest aus der Warteschlange entfernt!");
        de_DE.put("someone-is-in-support", "[B]Jemand is im Support Channel[/B]");
        de_DE.put("unknown-command", "Dieser Befehl ist nicht bekannt!");
        de_DE.put("you-wont-be-notified",
                "Du wirst nun keine Broadcast Nachrichten mehr erhalten. Dies ist nicht permanent");
        de_DE.put("you-will-be-notified", "Du wirst von nun an wieder Broadcast Nachrichten bekommen!");

        Section en_US = defaultIni.add(Language.EN_US.getID());
        en_US.put("commandslist", "Commands: [B]%commands%");
        en_US.put("webinterface-disalbed", "Webinterface is disabled!");
        en_US.put("webinterface-your-user", "Your username is: %wi-username%");
        en_US.put("webinterface-your-pw", "Your password is: %wi-password%");
        en_US.put("webinterface-login-is-temp", "This login is temporary!");
        en_US.put("welcome", "Welcome, %client-username%");
        en_US.put("you-were-moved-to-afk", "You were moved to AFK Channel!");
        en_US.put("you-were-moved-back-from-afk", "You were moved back!");
        en_US.put("you-mustnt-record-here", "[B]You must not record here![/B]");
        en_US.put("you-joined-support-channel", "The team got a message!");
        en_US.put("you-are-not-longer-in-support-queue", "You are not longer in the queue!");
        en_US.put("someone-is-in-support", "[B]Someone needs support[/B]");
        en_US.put("unknown-command", "Unknown Command!");
        en_US.put("you-wont-be-notified", "You won't get broadcast messages anymore. This is not permanent!");
        en_US.put("you-will-be-notified", "You will get broadcast messages.");

        Section pt_BR = defaultIni.add(Language.PT_BR.getID());

        pt_BR.put("commandslist", "Comandos: [B]%commands%");
        pt_BR.put("webinterface-your-user", "Seu usuário é: %wi-username%");
        pt_BR.put("webinterface-your-pw", "Sua senha é: %wi-password%");
        pt_BR.put("webinterface-login-is-temp", "Esta sessão é temporária!");
        pt_BR.put("welcome", "Bem vindo(a) %client-username%");
        pt_BR.put("you-are-not-longer-in-support-queue", "Você não está mais na fila de espera!");
        pt_BR.put("unknown-command", "Comando desconhecido!");
        pt_BR.put("you-will-be-notified", "Você receberá mensagens globais.");
        pt_BR.put("someone-is-in-support", "[B]Alguém precisa de suporte[/B]");
        pt_BR.put("you-mustnt-record-here", "[B]Você não deve gravar aqui![/B]");
        pt_BR.put("you-joined-support-channel", "O time recebeu a mensagem!");
        pt_BR.put("you-were-moved-to-afk", "Você foi movido para a sala AFK!");
        pt_BR.put("you-wont-be-notified", "Você não receberá mais mensagens globais. Isto não é permanente!");
        pt_BR.put("you-were-moved-back-from-afk", "Você foi movido de volta!");
        for (Section section : defaultIni.values()) {
            if (!ini.containsKey(section.getName())) {
                ini.put(section.getName(), section);
                changed = true;
            }
            Section sec = ini.get(section.getName());
            for (String key : section.keySet()) {
                if (!sec.containsKey(key)) {
                    sec.put(key, section.get(key));
                    changed = true;
                }
            }
        }
        if (changed) {
            System.out.println("Saving updated config...");
            ini.store();
            System.out.println("Done! Please setup the new Configuration Sections!");
        } else {
            System.out.println("Done! Nothing was changed :) Have fun!");
        }
        return ini;
    }

    public static String get(String id) {
        return msg.get(id);
    }

    public static void add(String id, String msgs) {
        msg.put(id, msgs);
    }
}
