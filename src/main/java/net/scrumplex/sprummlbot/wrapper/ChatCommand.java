package net.scrumplex.sprummlbot.wrapper;

import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import net.scrumplex.sprummlbot.plugins.CommandHandler;
import net.scrumplex.sprummlbot.plugins.SprummlbotPlugin;
import net.scrumplex.sprummlbot.tools.Exceptions;
import org.jetbrains.annotations.NotNull;

public class ChatCommand implements Comparable<ChatCommand> {
    private final String command;
    private final String usage;
    private CommandHandler commandHandler = null;
    private SprummlbotPlugin plugin = null;
    private PermissionGroup permissionGroup = null;

    public ChatCommand(String command, String usage) {
        this.command = command;
        this.usage = usage;
    }

    public CommandResponse handle(String message, ClientInfo client) {
        String commandName = message.toLowerCase();
        String[] args = new String[0];
        if (message.contains(" ")) {
            String[] parts = message.split(" ");
            commandName = parts[0].toLowerCase();
            args = new String[parts.length - 1];
            System.arraycopy(parts, 1, args, 0, parts.length - 1);
        }
        commandName = commandName.substring(1);
        if (!commandName.equalsIgnoreCase(getCommandName()))
            return CommandResponse.INTERNAL_ERROR;
        try {
            return commandHandler.handleCommand(client, args);
        } catch (Throwable throwable) {
            if (plugin == null)
                Exceptions.handle(throwable, "Internal Error", false);
            else
                Exceptions.handlePluginError(throwable, plugin);
            return CommandResponse.INTERNAL_ERROR;
        }
    }

    public String getCommandName() {
        return command;
    }

    public String getCommandUsage() {
        return this.usage;
    }

    public SprummlbotPlugin getCommandPlugin() {
        return this.plugin;
    }

    public void setCommandHandler(SprummlbotPlugin plugin, CommandHandler commandHandler) {
        this.plugin = plugin;
        this.commandHandler = commandHandler;
    }

    public void setPermissionGroup(PermissionGroup permissionGroup) {
        this.permissionGroup = permissionGroup;
    }

    @Deprecated
    public void setPermissionGroup(String permissionGroup) {
        this.permissionGroup = PermissionGroup.getPermissionGroupByName(permissionGroup);
    }

    @Override
    public int compareTo(@NotNull ChatCommand o) {
        return o.getCommandName().compareTo(getCommandName());
    }

    public PermissionGroup getPermissionGroup() {
        return permissionGroup;
    }
}
