package net.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import net.scrumplex.sprummlbot.configurations.Messages;
import net.scrumplex.sprummlbot.plugins.CommandHandler;
import net.scrumplex.sprummlbot.plugins.CommandManager;
import net.scrumplex.sprummlbot.tools.EasyMethods;
import net.scrumplex.sprummlbot.wrapper.ChatCommand;
import net.scrumplex.sprummlbot.wrapper.CommandResponse;

class SprummlbotCommands {
    static void registerCommands() {
        Vars.COMMAND_MGR = new CommandManager();
        Vars.COMMAND_MGR.registerCommand("help").setCommandHandler(null, new HelpCommandHandler());
        Vars.COMMAND_MGR.registerCommand("login").setCommandHandler(null, new LoginCommandHandler());
        Vars.COMMAND_MGR.registerCommand("support").setCommandHandler(null, new SupportCommandHandler());
        Vars.COMMAND_MGR.registerCommand("mute").setCommandHandler(null, new MuteCommandHandler());
        Vars.COMMAND_MGR.registerCommand("toggle", "!toggle module|command [*name*|list]").setCommandHandler(null, new ToggleCommandHandler());
        Vars.COMMAND_MGR.registerCommand("sendmsg", "!sendmsg private [exact client name] [msg]\n" + "!sendmsg public [msg]").setCommandHandler(null, new SendMSGCommandHandler());
        Vars.COMMAND_MGR.getCommands().get("login").setPermissionGroup(Vars.PERMGROUPS.get(Vars.PERMGROUPASSIGNMENTS.get("command_login")));
        Vars.COMMAND_MGR.getCommands().get("toggle").setPermissionGroup(Vars.PERMGROUPS.get(Vars.PERMGROUPASSIGNMENTS.get("command_toggle")));
        Vars.COMMAND_MGR.getCommands().get("sendmsg").setPermissionGroup(Vars.PERMGROUPS.get(Vars.PERMGROUPASSIGNMENTS.get("command_sendmsg")));

        for (String command : Vars.DISABLED_CONF_COMMANDS) {
            command = command.substring(1);
            if (!Vars.COMMAND_MGR.getCommands().containsKey(command))
                continue;
            ChatCommand chatCmd = Vars.COMMAND_MGR.getCommands().get(command);
            Vars.COMMAND_MGR.disableCommand(chatCmd, true);
        }
    }

    private static class HelpCommandHandler implements CommandHandler {
        @Override
        public CommandResponse handleCommand(ClientInfo c, String[] args) throws Exception {
            Vars.API.sendPrivateMessage(c.getId(), Messages.get("help-dialog") + "\n" + Messages.get("commandslist").replace("%commands%", Vars.COMMAND_MGR.buildHelpMessage(c))).awaitUninterruptibly();
            return CommandResponse.SUCCESS;
        }
    }

    private static class LoginCommandHandler implements CommandHandler {
        @Override
        public CommandResponse handleCommand(ClientInfo c, String[] args) {
            String user = "user" + c.getDatabaseId();
            String pass = EasyMethods.randomString();
            if (Vars.AVAILABLE_LOGINS.containsKey(user)) {
                pass = Vars.AVAILABLE_LOGINS.get(user);
            } else {
                Vars.AVAILABLE_LOGINS.put(user, pass);
            }
            Vars.API.sendPrivateMessage(c.getId(), Messages.get("webinterface-your-user").replace("%wi-username%", user) + "\n" +
                    Messages.get("webinterface-your-pw").replace("%wi-password%", pass) + "\n" +
                    "[URL=http://" + Vars.IP + ":" + Vars.WEBINTERFACE_PORT + "]" + Vars.IP + ":" + Vars.WEBINTERFACE_PORT + "[/URL]\n" +
                    Messages.get("webinterface-login-is-temp"));
            return CommandResponse.SUCCESS;
        }
    }

    private static class SupportCommandHandler implements CommandHandler {
        @Override
        public CommandResponse handleCommand(ClientInfo c, String[] args) {
            Vars.API.moveClient(c.getId(), Vars.SUPPORT_CHANNEL_IDS.get(0));
            return CommandResponse.SUCCESS;
        }
    }

    private static class MuteCommandHandler implements CommandHandler {
        @Override
        public CommandResponse handleCommand(ClientInfo c, String[] args) throws Exception {
            if (!Vars.PERMGROUPS.get(Vars.PERMGROUPASSIGNMENTS.get("broadcast")).isClientInGroup(c.getUniqueIdentifier())) {
                Vars.PERMGROUPS.get(Vars.PERMGROUPASSIGNMENTS.get("broadcast")).addClient(c.getUniqueIdentifier());
                Vars.API.sendPrivateMessage(c.getId(), Messages.get("you-wont-be-notified"));
            } else {
                Vars.PERMGROUPS.get(Vars.PERMGROUPASSIGNMENTS.get("broadcast")).removeClient(c.getUniqueIdentifier());
                Vars.API.sendPrivateMessage(c.getId(), Messages.get("you-will-be-notified"));
            }
            return CommandResponse.SUCCESS;
        }
    }

    private static class ToggleCommandHandler implements CommandHandler {
        @Override
        public CommandResponse handleCommand(ClientInfo c, String[] args) throws Exception {
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("module")) {
                    switch (args[1].toLowerCase()) {
                        case "support_notify":
                            Vars.SUPPORT_ENABLED = !Vars.SUPPORT_ENABLED;
                            Vars.API.sendPrivateMessage(c.getId(), "Module " + args[1] + " successfully " + (Vars.SUPPORT_ENABLED ? "enabled!" : "disabled!"));
                            System.out.println("Module " + args[1] + " was " + (Vars.SUPPORT_ENABLED ? "enabled" : "disabled") + " by " + c.getUniqueIdentifier());
                            return CommandResponse.SUCCESS;

                        case "afk_mover":
                            Vars.AFK_ENABLED = !Vars.AFK_ENABLED;
                            Vars.API.sendPrivateMessage(c.getId(), "Module " + args[1] + " successfully " + (Vars.AFK_ENABLED ? "enabled!" : "disabled!"));
                            System.out.println("Module " + args[1] + " was " + (Vars.AFK_ENABLED ? "enabled" : "disabled") + " by " + c.getUniqueIdentifier());
                            return CommandResponse.SUCCESS;

                        case "record_blocker":
                            Vars.ANTIREC_ENABLED = !Vars.ANTIREC_ENABLED;
                            Vars.API.sendPrivateMessage(c.getId(), "Module " + args[1] + " successfully " + (Vars.ANTIREC_ENABLED ? "enabled!" : "disabled!"));
                            System.out.println("Module " + args[1] + " was " + (Vars.ANTIREC_ENABLED ? "enabled" : "disabled") + " by " + c.getUniqueIdentifier());
                            return CommandResponse.SUCCESS;

                        case "vpn_checker":
                            Vars.VPNCHECKER_ENABLED = !Vars.VPNCHECKER_ENABLED;
                            Vars.API.sendPrivateMessage(c.getId(), "Module " + args[1] + " successfully " + (Vars.VPNCHECKER_ENABLED ? "enabled!" : "disabled!"));
                            System.out.println("Module " + args[1] + " was " + (Vars.VPNCHECKER_ENABLED ? "enabled" : "disabled") + " by " + c.getUniqueIdentifier());
                            return CommandResponse.SUCCESS;

                        case "server_group_protector":
                            Vars.GROUPPROTECT_ENABLED = !Vars.GROUPPROTECT_ENABLED;
                            Vars.API.sendPrivateMessage(c.getId(), "Module " + args[1] + " successfully " + (Vars.GROUPPROTECT_ENABLED ? "enabled!" : "disabled!"));
                            System.out.println("Module " + args[1] + " was " + (Vars.GROUPPROTECT_ENABLED ? "enabled" : "disabled") + " by " + c.getUniqueIdentifier());
                            return CommandResponse.SUCCESS;

                        case "broadcast":
                            Vars.BROADCAST_ENABLED = !Vars.BROADCAST_ENABLED;
                            Vars.API.sendPrivateMessage(c.getId(), "Module " + args[1] + " successfully " + (Vars.BROADCAST_ENABLED ? "enabled!" : "disabled!"));
                            System.out.println("Module " + args[1] + " was " + (Vars.BROADCAST_ENABLED ? "enabled" : "disabled") + " by " + c.getUniqueIdentifier());
                            return CommandResponse.SUCCESS;

                        case "list":
                            Vars.API.sendPrivateMessage(c.getId(), "Modules: \n" +
                                    "[COLOR=" + (Vars.AFK_ENABLED ? "#00bb00]" : "#bb0000]") + "afk_mover[/COLOR]\n" +
                                    "[COLOR=" + (Vars.BROADCAST_ENABLED ? "#00bb00]" : "#bb0000]") + "broadcast[/COLOR]\n" +
                                    "[COLOR=" + (Vars.ANTIREC_ENABLED ? "#00bb00]" : "#bb0000]") + "record_blocker[/COLOR]\n" +
                                    "[COLOR=" + (Vars.GROUPPROTECT_ENABLED ? "#00bb00]" : "#bb0000]") + "server_group_protector[/COLOR]\n" +
                                    "[COLOR=" + (Vars.SUPPORT_ENABLED ? "#00bb00]" : "#bb0000]") + "support_notify[/COLOR]\n" +
                                    "[COLOR=" + (Vars.VPNCHECKER_ENABLED ? "#00bb00]" : "#bb0000]") + "vpn_checker[/COLOR]"
                            );
                            return CommandResponse.SUCCESS;
                    }
                } else if (args[0].equalsIgnoreCase("command")) {
                    switch (args[1].toLowerCase()) {
                        case "list":
                            String cmds = "";
                            for (ChatCommand cmd : Vars.COMMAND_MGR.getCommands().values()) {
                                if (cmd.getCommandName().equalsIgnoreCase("help"))
                                    continue;
                                if (Vars.COMMAND_MGR.getDisabledCommands().containsKey(cmd))
                                    if (Vars.COMMAND_MGR.getDisabledCommands().get(cmd))
                                        continue;
                                cmds += "[COLOR=" + (Vars.COMMAND_MGR.getDisabledCommands().containsKey(cmd) ? "#bb0000]" : "#00bb00]") + "!" + cmd.getCommandName() + "[/COLOR]\n";
                            }
                            Vars.API.sendPrivateMessage(c.getId(), "Commands: \n" + cmds);
                            return CommandResponse.SUCCESS;
                        default:
                            String command = args[1].toLowerCase();
                            String commandName = command.substring(1);

                            System.out.println(command + " " + commandName);

                            if (command.equalsIgnoreCase("!help")) {
                                Vars.API.sendPrivateMessage(c.getId(), "Error");
                                return CommandResponse.ERROR;
                            }
                            if (Vars.COMMAND_MGR.getCommands().containsKey(commandName)) {
                                ChatCommand cmd = Vars.COMMAND_MGR.getCommands().get(commandName);
                                if (!Vars.COMMAND_MGR.getDisabledCommands().containsKey(cmd))
                                    Vars.COMMAND_MGR.disableCommand(cmd);
                                else {
                                    if (Vars.COMMAND_MGR.getDisabledCommands().get(cmd)) {
                                        Vars.API.sendPrivateMessage(c.getId(), "Error");
                                        return CommandResponse.SUCCESS;
                                    }
                                    Vars.COMMAND_MGR.enableCommand(cmd);
                                }
                                Vars.API.sendPrivateMessage(c.getId(), "Command " + command + " successfully " + (!Vars.COMMAND_MGR.getDisabledCommands().containsKey(cmd) ? "enabled!" : "disabled!"));
                                System.out.println("Command " + command + " was " + (!Vars.COMMAND_MGR.getDisabledCommands().containsKey(cmd) ? "enabled" : "disabled") + " by " + c.getUniqueIdentifier());
                                return CommandResponse.SUCCESS;
                            }
                            Vars.API.sendPrivateMessage(c.getId(), "Command not found!");
                            return CommandResponse.ERROR;
                    }
                }
            }
            return CommandResponse.SYNTAX_ERROR;
        }
    }
    private static class SendMSGCommandHandler implements CommandHandler {
        @Override
        public CommandResponse handleCommand(ClientInfo c, String[] args) throws Exception {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("private")) {
                    if (args.length >= 3) {
                        String name = args[1];
                        Client target = Vars.API.getClientByNameExact(name, false).getUninterruptibly();
                        if (target != null) {
                            String msg = "From [URL=client://" + c.getId() + "/" + c.getUniqueIdentifier() + "~" + c.getNickname() + "]" + c.getNickname() + "[/URL]:";
                            for (int i = 2; i < args.length; i++) {
                                msg += " " + args[i];
                            }
                            Vars.API.sendPrivateMessage(target.getId(), msg);
                            return CommandResponse.SUCCESS;
                        } else {
                            Vars.API.sendPrivateMessage(c.getId(), "Client not found!");
                            return CommandResponse.ERROR;
                        }
                    }
                } else if (args[0].equalsIgnoreCase("public")) {
                    if (args.length >= 2) {
                        String msg = "[URL=client://" + c.getId() + "/" + c.getUniqueIdentifier() + "~" + c.getNickname() + "]" + c.getNickname() + "[/URL]:";
                        for (int i = 1; i < args.length; i++) {
                            msg += " " + args[i];
                        }
                        Vars.API.sendServerMessage(msg);
                        return CommandResponse.SUCCESS;
                    }
                }
            }
            return CommandResponse.SYNTAX_ERROR;
        }
    }
}
