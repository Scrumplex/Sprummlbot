package net.scrumplex.sprummlbot.plugins;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import net.scrumplex.sprummlbot.Sprummlbot;
import net.scrumplex.sprummlbot.config.Messages;
import net.scrumplex.sprummlbot.wrapper.ChatCommand;
import net.scrumplex.sprummlbot.wrapper.CommandResponse;
import net.scrumplex.sprummlbot.wrapper.PermissionGroup;

import java.util.Map;
import java.util.TreeMap;

public class CommandManager {
    private final Map<ChatCommand, Boolean> disabled = new TreeMap<>();
    private final Map<String, ChatCommand> commands = new TreeMap<>();
    private final Sprummlbot sprummlbot;

    public CommandManager(Sprummlbot sprummlbot) {
        this.sprummlbot = sprummlbot;
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
        if (isCommandDisabled(command))
            if (!disabled.get(command))
                disabled.remove(command);
    }

    @Deprecated
    public void setCommandPermissionGroup(ChatCommand command, PermissionGroup group) {
        System.out.println("[Plugins] CommandManager#setCommandPermissionGroup() is deprecated! Please use ChatCommand#setPermissionGroup() instead!");
        command.setPermissionGroup(group);
    }

    public boolean isCommandEnabled(ChatCommand command) {
        return !disabled.containsKey(command) && commands.containsValue(command);
    }

    public boolean isCommandDisabled(ChatCommand command) {
        return !isCommandEnabled(command);
    }

    public String buildHelpMessage(String uid) {
        String helpMessage = "";
        if (isCommandDisabled(commands.get("help")))
            enableCommand(commands.get("help"));

        StringBuilder b = new StringBuilder("");
        for (String cmd : commands.keySet()) {
            ChatCommand command = commands.get(cmd);
            if (isCommandDisabled(command))
                continue;
            PermissionGroup group = command.getPermissionGroup();
            if (group != null)
                if (!group.isClientInGroup(uid))
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
        String cmd = msg.toLowerCase();
        if (msg.contains(" ")) {
            cmd = msg.split(" ")[0].toLowerCase();
        }
        cmd = cmd.substring(1);
        if (!commands.containsKey(cmd))
            return CommandResponse.NOT_FOUND;
        ChatCommand command = commands.get(cmd);
        if (disabled.containsKey(command))
            return CommandResponse.NOT_FOUND;

        PermissionGroup group = command.getPermissionGroup();
        if (group != null)
            if (!group.isClientInGroup(client.getUniqueIdentifier()))
                return CommandResponse.FORBIDDEN;

        CommandResponse response = command.handle(msg, client);
        if (response == CommandResponse.SYNTAX_ERROR)
            sprummlbot.getAsyncAPI().sendPrivateMessage(client.getId(), Messages.get("command-syntax-err").replace("%commandsyntax%", command.getCommandUsage()));
        return response;
    }

    public Map<String, ChatCommand> getCommands() {
        return commands;
    }

    public Map<ChatCommand, Boolean> getDisabledCommands() {
        return disabled;
    }

}
