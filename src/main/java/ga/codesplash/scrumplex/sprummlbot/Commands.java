package ga.codesplash.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.api.CommandFuture;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import ga.codesplash.scrumplex.sprummlbot.configurations.Messages;
import ga.codesplash.scrumplex.sprummlbot.plugins.SprummlbotPlugin;
import ga.codesplash.scrumplex.sprummlbot.tools.EasyMethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Commands {

    private static final ArrayList<String> DISABLED = new ArrayList<>();
    private static final Map<String, Boolean> COMMANDS = new HashMap<>();
    public static String AVAILABLE_COMMANDS = "";
    private static boolean setUp = false;

    /**
     * Registers default Commands
     */
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

    /**
     * Registers default Commands
     *
     * @param disabled Adds disabled commands to the default commands
     */
    public static void setup(String[] disabled) {
        if (!setUp) {
            registerDefaultCommands();
            for (String cmd : disabled) {
                if (!cmd.equalsIgnoreCase("!help")) {
                    disableCommand(cmd);
                }
            }
        }
        setUp = true;
    }


    /**
     * Registers a command and puts it into !help menu
     *
     * @param command Command, which should be registered
     * @param hidden  Defines if command is hidden or not
     */
    public static void registerCommand(String command, boolean hidden) {
        COMMANDS.put(command, hidden);
        buildHelpMessage();
    }

    /**
     * Disables a registered command and removes it form !help menu
     *
     * @param command The command which will be disabled
     */
    public static void disableCommand(String command) {
        COMMANDS.remove(command);
        DISABLED.add(command);
        buildHelpMessage();
    }

    /**
     * Re-enables a disabled command and puts it into !help menu
     *
     * @param command The command which will be re-enabled
     * @param hidden  defines if the command should be hidden or not
     */
    public static void enableCommand(String command, boolean hidden) {
        DISABLED.remove(command);
        registerCommand(command, hidden);
    }

    /**
     * Builds the message of the !help command
     */
    private static void buildHelpMessage() {
        AVAILABLE_COMMANDS = "";
        if (!COMMANDS.containsKey("!help")) {
            enableCommand("!help", false);
        }
        StringBuilder b = new StringBuilder("");
        for (String cmd : COMMANDS.keySet()) {
            if (!COMMANDS.get(cmd)) {
                b.append(cmd).append(", ");
            }
        }
        if (b.toString().length() > 0 && b.toString().charAt(b.toString().length() - 2) == ',') {
            AVAILABLE_COMMANDS = b.toString().substring(0, b.toString().length() - 2);
        }
    }

    /**
     * Default Command Handler
     *
     * @param cmd Requested command
     * @param c   Client who requested
     * @return Returns if command exists
     */
    static boolean handle(String cmd, Client c) {
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
            if (command.equalsIgnoreCase("!help")) {
                return commandHelp(c);
            }

            if (!DISABLED.contains(command)) {
                switch (command) {
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
                for (SprummlbotPlugin plugin : Main.pluginManager.getPlugins()) {
                    if (plugin.onCommand(command, args, c)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * Default Command
     *
     * @param c Invoker
     * @return Returns if command exists
     */
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

    /**
     * Default Command
     *
     * @param c Invoker
     * @return Returns if command exists
     */
    private static boolean commandSUPPORT(Client c) {
        Vars.API.moveClient(c.getId(), Vars.SUPPORT_CHANNEL_ID);
        return true;
    }

    /**
     * Default Command
     *
     * @param c Invoker
     * @return Returns if command exists
     */
    private static boolean commandHelp(Client c) {
        Vars.API.sendPrivateMessage(c.getId(), Messages.get("help-dialog"));
        Vars.API.sendPrivateMessage(c.getId(), Messages.get("commandslist").replace("%commands%", AVAILABLE_COMMANDS));
        return true;
    }

    /**
     * Default Command
     *
     * @param c Invoker
     * @return Returns if command exists
     */
    private static boolean commandYT(Client c) {
        Vars.API.sendPrivateMessage(c.getId(), "[URL=" + Messages.get("youtube") + "]Youtube Channel[/URL]");
        return true;
    }

    /**
     * Default Command
     *
     * @param c Invoker
     * @return Returns if command exists
     */
    private static boolean commandWEB(Client c) {
        Vars.API.sendPrivateMessage(c.getId(), "[URL=" + Messages.get("website") + "]Website[/URL]");
        return true;
    }

    /**
     * Default Command
     *
     * @param c Invoker
     * @return Returns if command exists
     */
    private static boolean commandSKYPE(Client c) {
        Vars.API.sendPrivateMessage(c.getId(), "Skype ID: " + Messages.get("skype"));
        return true;
    }

    /**
     * Default Command
     *
     * @param c Invoker
     * @return Returns if command exists
     */
    private static boolean commandLOGIN(Client c) {
        if (Vars.LOGINABLE.contains(c.getUniqueIdentifier())) {
            String user = "user" + c.getDatabaseId();
            String pass = EasyMethods.randomString(10);

            if (Vars.AVAILABLE_LOGINS.containsKey(user)) {
                pass = Vars.AVAILABLE_LOGINS.get(user);
            } else {
                Vars.AVAILABLE_LOGINS.put(user, pass);
            }

            Vars.API.sendPrivateMessage(c.getId(), Messages.get("webinterface-your-user").replace("%wi-username%", user));
            Vars.API.sendPrivateMessage(c.getId(), Messages.get("webinterface-your-pw").replace("%wi-password%", pass));
            Vars.API.sendPrivateMessage(c.getId(), Messages.get("webinterface-login-is-temp"));
            return true;
        }
        return false;
    }

    /**
     * Default Command
     */
    private static void consoleCommandList() {
        final List<String> clients = new ArrayList<>();

        Vars.API.getClients().onSuccess(new CommandFuture.SuccessListener<List<Client>>() {
            @Override
            public void handleSuccess(List<Client> result) {
                for (final Client c : result) {
                    Vars.API.getClientInfo(c.getId()).onSuccess(new CommandFuture.SuccessListener<ClientInfo>() {
                        @Override
                        public void handleSuccess(ClientInfo ci) {
                            System.out.println("Name=" + c.getNickname() + ", IP=" + ci.getIp() + ", ID=" + c.getId() + ", UID="
                                    + c.getUniqueIdentifier());
                        }
                    });
                }
            }
        });
    }

    /**
     * Default Command
     */
    private static void consoleCommandStop() {
        System.exit(0);
    }
}
