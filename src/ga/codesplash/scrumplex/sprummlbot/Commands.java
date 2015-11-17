package ga.codesplash.scrumplex.sprummlbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;

import ga.codesplash.scrumplex.sprummlbot.configurations.Messages;
import ga.codesplash.scrumplex.sprummlbot.plugins.SprummlPlugin;

public class Commands {

    public static String AVAILABLE_COMMANDS = "";

    private static ArrayList<String> DISABLED = new ArrayList<>();
    private static Map<String, Boolean> COMMANDS = new HashMap<>();

    private static void registerDefaultCommands() {
        registerCommand("!help", false);
        registerCommand("!yt", false);
        registerCommand("!web", false);
        registerCommand("!skype", false);
        registerCommand("!login", true);
        registerCommand("!support", false);
        registerCommand("!mute", false);
        if (!Vars.SUPPORT_ENABLED) {
            disableCommand("!support");
        }
        if (!Vars.BROADCAST_ENABLED) {
            disableCommand("!mute");
        }
        if (Vars.WEBINTERFACE_PORT == 0) {
            disableCommand("!login");
        }
    }

    public static void setup(String[] disabled) {
        registerDefaultCommands();
        for (String cmd : disabled) {
            if (!cmd.equalsIgnoreCase("!help")) {
                disableCommand(cmd);
            }
        }
    }

    public static void registerCommand(String command, boolean hidden) {
        COMMANDS.put(command, hidden);
        AVAILABLE_COMMANDS = "";
        for (String cmd : COMMANDS.keySet()) {
            if (!COMMANDS.get(cmd)) {
                AVAILABLE_COMMANDS += cmd + ", ";
            }
        }
    }

    public static void disableCommand(String command) {
        COMMANDS.remove(command);
        DISABLED.add(command);
        AVAILABLE_COMMANDS = "";
        for (String cmd : COMMANDS.keySet()) {
            if (!COMMANDS.get(cmd)) {
                AVAILABLE_COMMANDS += cmd + ", ";
            }
        }
    }

    public static boolean handle(String cmd, Client c) {
        if (c == null) {
            if (cmd.equalsIgnoreCase("list")) {
                consoleCommandList();
                return true;
            } else if (cmd.equalsIgnoreCase("stop")) {
                consoleCommandStop();
                return true;
            }
            return false;
        } else {
            String command = cmd;
            String[] args = new String[0];
            if (cmd.contains(" ")) {
                String[] parts = cmd.split(" ");
                command = parts[0];
                args = new String[parts.length - 1];
                System.arraycopy(parts, 1, args, 0, parts.length - 1);
            }
            if (!DISABLED.contains(command)) {
                switch (command) {

                    case "!help":
                        return commandHelp(c);

                    case "!yt":
                        return commandYT(c);

                    case "!web":
                        return commandWEB(c);

                    case "!skype":
                        return commandSKYPE(c);

                    case "!login":
                        return commandLOGIN(c);

                    case "!support":
                        return commandSUPPORT(c);

                    case "!mute":
                        return commandMUTE(c);
                }
                for (SprummlPlugin plugin : SprummlbotLoader.pl.pluginCommands) {
                    if (plugin.handleCommand(c, command, args)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private static boolean commandMUTE(Client c) {
        if (!Vars.BROADCAST_IGNORE.contains(c.getUniqueIdentifier())) {
            Vars.BROADCAST_IGNORE.add(c.getUniqueIdentifier());
            Vars.API.sendPrivateMessage(c.getId(), Messages.get("you-wont-be-notified"));
        } else {
            Vars.BROADCAST_IGNORE.remove(c.getUniqueIdentifier());
            Vars.API.sendPrivateMessage(c.getId(), Messages.get("you-will-be-notified"));
        }
        return true;
    }

    private static boolean commandSUPPORT(Client c) {
        Vars.API.moveClient(c.getId(), Vars.SUPPORTCHANNELID);
        return true;
    }

    private static boolean commandHelp(Client c) {
        Vars.API.sendPrivateMessage(c.getId(), Messages.get("help-dialog"));
        Vars.API.sendPrivateMessage(c.getId(), Messages.get("commandslist") + AVAILABLE_COMMANDS);
        return true;
    }

    private static boolean commandYT(Client c) {
        Vars.API.sendPrivateMessage(c.getId(), "[URL=" + Messages.get("youtube") + "]Youtube Channel[/URL]");
        return true;
    }

    private static boolean commandWEB(Client c) {
        Vars.API.sendPrivateMessage(c.getId(), "[URL=" + Messages.get("website") + "]Website[/URL]");
        return true;
    }

    private static boolean commandSKYPE(Client c) {
        Vars.API.sendPrivateMessage(c.getId(), "Skype ID: " + Messages.get("skype"));
        return true;
    }

    private static boolean commandLOGIN(Client c) {
        if (Vars.LOGINABLE.contains(c.getUniqueIdentifier())) {
            if (Vars.WEBINTERFACE_PORT == 0) {
                Vars.API.sendPrivateMessage(c.getId(), Messages.get("webinterface-disabled"));
            } else {
                String user = "user" + c.getDatabaseId();
                String pass;
                if (WebGUILogins.AVAILABLE.containsKey(user)) {
                    pass = WebGUILogins.AVAILABLE.get(user);
                    Vars.API.sendPrivateMessage(c.getId(), Messages.get("webinterface-your-user") + user);
                    Vars.API.sendPrivateMessage(c.getId(), Messages.get("webinterface-your-pw") + pass);
                    Vars.API.sendPrivateMessage(c.getId(), Messages.get("webinterface-login-is-temp"));
                } else {

                    pass = randomString(10);
                    Vars.API.sendPrivateMessage(c.getId(), Messages.get("webinterface-your-user") + user);
                    Vars.API.sendPrivateMessage(c.getId(), Messages.get("webinterface-your-pw") + pass);
                    Vars.API.sendPrivateMessage(c.getId(), Messages.get("webinterface-login-is-temp"));
                    WebGUILogins.AVAILABLE.put(user, pass);
                }
            }
            return true;
        }
        return false;
    }

    private static void consoleCommandList() {
        List<String> clients = new ArrayList<>();

        for (Client c : Vars.API.getClients()) {
            ClientInfo ci = Vars.API.getClientInfo(c.getId());
            clients.add("Name=" + c.getNickname() + ", IP=" + ci.getIp() + ", ID=" + c.getId() + ", UID="
                    + c.getUniqueIdentifier());
        }
        for (String c : clients) {
            System.out.println(c);
        }
    }

    private static void consoleCommandStop() {
        System.exit(0);
    }

    private final static String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static Random rnd = new Random();

    private static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }
}
