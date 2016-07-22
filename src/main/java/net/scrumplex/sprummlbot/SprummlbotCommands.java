package net.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import net.scrumplex.sprummlbot.config.Messages;
import net.scrumplex.sprummlbot.plugins.CommandHandler;
import net.scrumplex.sprummlbot.plugins.CommandManager;
import net.scrumplex.sprummlbot.tools.EasyMethods;
import net.scrumplex.sprummlbot.wrapper.CommandResponse;
import net.scrumplex.sprummlbot.wrapper.PermissionGroup;

class SprummlbotCommands {
    private static CommandManager commandManager;

    static void registerCommands() {
        commandManager = Sprummlbot.getSprummlbot().getCommandManager();
        commandManager.registerCommand("help").setCommandHandler(null, new HelpCommandHandler());
        commandManager.registerCommand("login").setCommandHandler(null, new LoginCommandHandler());
        commandManager.registerCommand("sendmsg", "!sendmsg private [exact client name] [msg]\n" + "!sendmsg public [msg]").setCommandHandler(null, new SendMSGCommandHandler());
        commandManager.getCommands().get("login").setPermissionGroup(PermissionGroup.getPermissionGroupByName(Vars.PERMGROUPASSIGNMENTS.get("command_login")));
        commandManager.getCommands().get("sendmsg").setPermissionGroup(PermissionGroup.getPermissionGroupByName(Vars.PERMGROUPASSIGNMENTS.get("command_sendmsg")));
    }

    private static class HelpCommandHandler implements CommandHandler {
        @Override
        public CommandResponse handleCommand(ClientInfo c, String[] args) throws Exception {
            Sprummlbot.getSprummlbot().getDefaultAPI().sendPrivateMessage(c.getId(), Messages.get("help-dialog") + "\n" + Messages.get("commandslist").replace("%commands%", commandManager.buildHelpMessage(c))).awaitUninterruptibly();
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
            Sprummlbot.getSprummlbot().getDefaultAPI().sendPrivateMessage(c.getId(), Messages.get("webinterface-your-user").replace("%wi-username%", user) + "\n" +
                    Messages.get("webinterface-your-pw").replace("%wi-password%", pass) + "\n" +
                    "[URL=http://" + Vars.IP + ":" + Vars.WEBINTERFACE_PORT + "]" + Vars.IP + ":" + Vars.WEBINTERFACE_PORT + "[/URL]\n" +
                    Messages.get("webinterface-login-is-temp"));
            return CommandResponse.SUCCESS;
        }
    }

    private static class SendMSGCommandHandler implements CommandHandler {
        @Override
        public CommandResponse handleCommand(ClientInfo c, String[] args) throws Exception {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("private")) {
                    if (args.length >= 3) {
                        String name = args[1];
                        Client target = Sprummlbot.getSprummlbot().getDefaultAPI().getClientByNameExact(name, false).getUninterruptibly();
                        if (target != null) {
                            String msg = "From [URL=client://" + c.getId() + "/" + c.getUniqueIdentifier() + "~" + c.getNickname() + "]" + c.getNickname() + "[/URL]:";
                            for (int i = 2; i < args.length; i++) {
                                msg += " " + args[i];
                            }
                            Sprummlbot.getSprummlbot().getDefaultAPI().sendPrivateMessage(target.getId(), msg);
                            return CommandResponse.SUCCESS;
                        } else {
                            Sprummlbot.getSprummlbot().getDefaultAPI().sendPrivateMessage(c.getId(), "Client not found!");
                            return CommandResponse.ERROR;
                        }
                    }
                } else if (args[0].equalsIgnoreCase("public")) {
                    if (args.length >= 2) {
                        String msg = "[URL=client://" + c.getId() + "/" + c.getUniqueIdentifier() + "~" + c.getNickname() + "]" + c.getNickname() + "[/URL]:";
                        for (int i = 1; i < args.length; i++) {
                            msg += " " + args[i];
                        }
                        Sprummlbot.getSprummlbot().getDefaultAPI().sendServerMessage(msg);
                        return CommandResponse.SUCCESS;
                    }
                }
            }
            return CommandResponse.SYNTAX_ERROR;
        }
    }
}
