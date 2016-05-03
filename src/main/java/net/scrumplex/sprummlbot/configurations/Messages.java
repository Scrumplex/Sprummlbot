package net.scrumplex.sprummlbot.configurations;

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
                "[URL=" + Vars.AD_LINK + "]Sprummlbot[/URL] v" + Vars.VERSION + " by " + Vars.AUTHOR + ".");
    }

    private static Ini getDefaultIni() {
        Ini defaultIni = new Ini();
        Section de_DE = defaultIni.add("de_DE");
        de_DE.put("commandslist", "Verfügbare Befehle: [B]%commands%");
        de_DE.put("webinterface-your-user", "Dein Benutzername ist: %wi-username%");
        de_DE.put("webinterface-your-pw", "Dein Passwort ist: %wi-password%");
        de_DE.put("webinterface-login-is-temp", "Dieses Login ist temporär!");
        de_DE.put("welcome", "Willkommen, %client-username%");
        de_DE.put("you-were-moved-to-afk", "Du wurdest in den AFK Channel verschoben.");
        de_DE.put("you-were-moved-back-from-afk", "Du wurdest zurück verschoben!");
        de_DE.put("you-mustnt-record-here", "Du darfst auf diesem Server nicht aufnehmen!");
        de_DE.put("you-joined-support-channel", "Die Team-Mitglieder wurden kontaktiert!");
        de_DE.put("you-are-not-longer-in-support-queue", "Du wurdest aus der Warteschlange entfernt!");
        de_DE.put("someone-is-in-support", "Jemand hat den Support Channel betreten!");
        de_DE.put("unknown-command", "Dieser Befehl ist nicht bekannt!");
        de_DE.put("command-error", "Dieser Befehl konnte nicht verarbeitet werden!");
        de_DE.put("command-no-permission", "Du benötigst höhere Rechte, um diesen Befehl auszuführen!");
        de_DE.put("command-syntax-err", "Verwendung: %commandsyntax%");
        de_DE.put("you-wont-be-notified",
                "Du wirst nun keine Rundruf-Nachrichten mehr erhalten. Dies ist nicht permanent!");
        de_DE.put("you-will-be-notified", "Du wirst von nun an wieder Rundruf-Nachrichten erhalten!");
        de_DE.put("you-are-using-vpn", "Du benutzt einen VPN Dienst. Bitte deaktiviere diesen.");

        Section en_US = defaultIni.add("en_US");
        en_US.put("commandslist", "Commands: [B]%commands%");
        en_US.put("webinterface-your-user", "Your username is: %wi-username%");
        en_US.put("webinterface-your-pw", "Your password is: %wi-password%");
        en_US.put("webinterface-login-is-temp", "This login is temporary!");
        en_US.put("welcome", "Welcome, %client-username%");
        en_US.put("you-were-moved-to-afk", "You were moved to the AFK channel.");
        en_US.put("you-were-moved-back-from-afk", "You were moved back.");
        en_US.put("you-mustnt-record-here", "You are not allowed to capture sound!");
        en_US.put("you-joined-support-channel", "The Team-Members were contacted.");
        en_US.put("you-are-not-longer-in-support-queue", "You have been removed from the queue.");
        en_US.put("someone-is-in-support", "Someone has entered the support channel!");
        en_US.put("unknown-command", "Unknown Command.");
        en_US.put("command-error", "An error occurred while processing the command!");
        en_US.put("command-no-permission", "You are not allowed to use this command!");
        en_US.put("command-syntax-err", "Usage: %commandsyntax%");
        en_US.put("you-wont-be-notified", "You will not receive broadcast messages anymore. This is not permanent!");
        en_US.put("you-will-be-notified", "You will receive broadcast messages.");
        en_US.put("you-are-using-vpn", "You are using a VPN Service. Please disable it!");

        Section pt_BR = defaultIni.add("pt_BR");
        pt_BR.put("commandslist", "Comandos: [B]%commands%");
        pt_BR.put("webinterface-your-user", "Seu usuário é: %wi-username%");
        pt_BR.put("webinterface-your-pw", "Sua senha é: %wi-password%");
        pt_BR.put("webinterface-login-is-temp", "Esta sessão é temporária!");
        pt_BR.put("welcome", "Bem vindo(a) %client-username%");
        pt_BR.put("you-are-not-longer-in-support-queue", "Você não está mais na fila de espera!");
        pt_BR.put("unknown-command", "Comando desconhecido!");
        pt_BR.put("command-error", "An error occurred while processing the command!");
        pt_BR.put("command-no-permission", "You do not have permission to use this command!");
        pt_BR.put("command-syntax-err", "Usage: %commandsyntax%");
        pt_BR.put("you-will-be-notified", "Você receberá mensagens globais.");
        pt_BR.put("someone-is-in-support", "[B]Alguém precisa de suporte[/B]");
        pt_BR.put("you-mustnt-record-here", "[B]Você não deve gravar aqui![/B]");
        pt_BR.put("you-joined-support-channel", "O time recebeu a mensagem!");
        pt_BR.put("you-were-moved-to-afk", "Você foi movido para a sala AFK!");
        pt_BR.put("you-wont-be-notified", "Você não receberá mais mensagens globais. Isto não é permanente!");
        pt_BR.put("you-were-moved-back-from-afk", "Você foi movido de volta!");
        pt_BR.put("you-are-using-vpn", "Você está usando um serviço VPN. Por favor desative-o!");

        Section it = defaultIni.add("it");
        it.put("commandslist", "Comandi: [B]%commands%");
        it.put("webinterface-your-user", "Il tuo nome utente è: %wi-username%");
        it.put("webinterface-your-pw", "La tua password è: %wi-password%");
        it.put("webinterface-login-is-temp", "Questo accesso è temporaneo!");
        it.put("welcome", "Benvenuto, %client-username%");
        it.put("you-are-not-longer-in-support-queue", "Non sei più in lista d'attesa!");
        it.put("unknown-command", "Comando non riconosciuto!");
        it.put("command-error", "An error occurred while processing the command!");
        it.put("command-no-permission", "You do not have permission to use this command!");
        it.put("command-syntax-err", "Usage: %commandsyntax%");
        it.put("you-will-be-notified", "Riceverai messaggi globali.");
        it.put("someone-is-in-support", "[B]Qualcuno ha bisogno di aiuto[/B]");
        it.put("you-mustnt-record-here", "[B]Non puoi registrare qui![/B]");
        it.put("you-joined-support-channel", "Il team ha ricevuto un messaggio!");
        it.put("you-were-moved-to-afk", "Sei stato spostato nel canale AFK!");
        it.put("you-wont-be-notified", "Non riceverai più messaggi globali. Non è permanente!");
        it.put("you-were-moved-back-from-afk", "Sei stato spostato nel canale precedente!");
        it.put("you-are-using-vpn", "Stai usando un Servizio VPN. Perfavore disabilitalo!");
        return defaultIni;
    }

    public static String get(String id) {
        return msg.get(id);
    }

    public static void add(String id, String msgs) {
        msg.put(id, msgs);
    }
}
