package net.scrumplex.sprummlbot.plugins;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import net.scrumplex.sprummlbot.Vars;
import net.scrumplex.sprummlbot.configurations.Messages;
import net.scrumplex.sprummlbot.wrapper.ChatCommand;
import net.scrumplex.sprummlbot.wrapper.CommandResponse;
import net.scrumplex.sprummlbot.wrapper.PermissionGroup;

import java.util.Map;
import java.util.TreeMap;

public class CommandManager {
    private final Map<ChatCommand, Boolean> disabled = new TreeMap<>();
    private final Map<String, ChatCommand> commands = new TreeMap<>();
    private final Map<ChatCommand, PermissionGroup> permission = new TreeMap<>();

    public CommandManager() {

    }

    public ChatCommand registerCommand(String command) {
        ChatCommand c = new ChatCommand(command, "!" + command);
        commands.put(command, c);
        return c;
    }

    public ChatCommand registerCommand(String command, String usage) {
        ChatCommand c = new ChatCommand(command, usage);
        commands.put(command, c);
        return c;
    }

    public void disableCommand(ChatCommand command) {
        disableCommand(command, false);
    }

    public void disableCommand(ChatCommand command, boolean permanent) {
        disabled.put(command, permanent);
    }

    public void enableCommand(ChatCommand command) {
        if (!isCommandEnabled(command))
            if (!disabled.get(command))
                disabled.remove(command);
    }

    public void setCommandPermissionGroup(ChatCommand command, PermissionGroup group) {
        permission.put(command, group);
    }

    public boolean isCommandEnabled(ChatCommand command) {
        return !disabled.containsKey(command) && commands.containsValue(command);
    }

    public String buildHelpMessage(String uid) {
        String helpMessage = "";
        if (!isCommandEnabled(commands.get("help")))
            enableCommand(commands.get("help"));

        StringBuilder b = new StringBuilder("");
        for (String cmd : commands.keySet()) {
            ChatCommand command = commands.get(cmd);
            if (!isCommandEnabled(command))
                continue;
            if (permission.get(command) != null)
                if (!permission.get(command).isClientInGroup(uid))
                    continue;
            b.append("!").append(cmd).append(", ");
        }
        if (b.toString().length() > 0 && b.toString().charAt(b.toString().length() - 2) == ',')
            helpMessage = b.toString().substring(0, b.toString().length() - 2);
        return helpMessage;
    }

    public String buildHelpMessage(Client c) {
        return buildHelpMessage(c.getUniqueIdentifier());
    }

    public CommandResponse handleClientCommand(String msg, ClientInfo client) {
        if (msg.equals("!"))
            msg = "!help";
        String command = msg.toLowerCase();
        if (msg.contains(" ")) {
            command = msg.split(" ")[0].toLowerCase();
        }
        command = command.substring(1);
        if (!commands.containsKey(command))
            return CommandResponse.NOT_FOUND;
        ChatCommand cmd = commands.get(command);
        if (disabled.containsKey(cmd))
            return CommandResponse.NOT_FOUND;
        if (permission.containsKey(cmd))
            if (!permission.get(cmd).isClientInGroup(client.getUniqueIdentifier()))
                return CommandResponse.FORBIDDEN;

        CommandResponse response = cmd.handle(msg, client);
        if (response == CommandResponse.SYNTAX_ERROR)
            Vars.API.sendPrivateMessage(client.getId(), Messages.get("command-syntax-err").replace("%commandsyntax%", cmd.getCommandUsage()));
        return response;
    }

    public Map<String, ChatCommand> getCommands() {
        return commands;
    }

    public Map<ChatCommand, Boolean> getDisabledCommands() {
        return disabled;
    }

}
