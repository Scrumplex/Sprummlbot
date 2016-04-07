package net.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.api.VirtualServerProperty;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import net.scrumplex.sprummlbot.configurations.Configuration;
import net.scrumplex.sprummlbot.configurations.Messages;
import net.scrumplex.sprummlbot.plugins.CommandHandler;
import net.scrumplex.sprummlbot.plugins.CommandManager;
import net.scrumplex.sprummlbot.plugins.PluginLoader;
import net.scrumplex.sprummlbot.plugins.PluginManager;
import net.scrumplex.sprummlbot.tools.EasyMethods;
import net.scrumplex.sprummlbot.tools.Exceptions;
import net.scrumplex.sprummlbot.tools.SprummlbotOutStream;
import net.scrumplex.sprummlbot.vpn.VPNConfig;
import net.scrumplex.sprummlbot.webinterface.FileLoader;
import net.scrumplex.sprummlbot.webinterface.WebServerManager;
import net.scrumplex.sprummlbot.wrapper.ChatCommand;
import net.scrumplex.sprummlbot.wrapper.CommandResponse;
import net.scrumplex.sprummlbot.wrapper.State;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Startup {

    public static PluginLoader pluginLoader;
    public static PluginManager pluginManager;
    public static SprummlbotOutStream out;
    public static VPNConfig vpnConfig;
    public static DynamicBanner banner;

    static void start() {
        Tasks.init();

        pluginManager = new PluginManager();
        pluginLoader = new PluginLoader(pluginManager);

        File config = new File("config.ini");
        boolean firstStart = !config.exists();
        System.out.println("Loading Config...");
        try {
            Configuration.load(config, firstStart);
        } catch (Exception e) {
            Exceptions.handle(e, "CONFIG LOADING FAILED!");
        }

        Vars.COMMAND_MGR = new CommandManager();
        Vars.COMMAND_MGR.registerCommand("help").setCommandHandler(null, new CommandHandler() {
            @Override
            public CommandResponse handleCommand(ClientInfo c, String[] args) {
                Vars.API.sendPrivateMessage(c.getId(), Messages.get("help-dialog") + "\n" + Messages.get("commandslist").replace("%commands%", Vars.COMMAND_MGR.buildHelpMessage(c))).awaitUninterruptibly();
                return CommandResponse.SUCCESS;
            }
        });
        Vars.COMMAND_MGR.registerCommand("yt").setCommandHandler(null, new CommandHandler() {
            @Override
            public CommandResponse handleCommand(ClientInfo c, String[] args) {
                Vars.API.sendPrivateMessage(c.getId(), "[URL=" + Messages.get("youtube") + "]Youtube Channel[/URL]");
                return CommandResponse.SUCCESS;
            }
        });
        Vars.COMMAND_MGR.registerCommand("web").setCommandHandler(null, new CommandHandler() {
            @Override
            public CommandResponse handleCommand(ClientInfo c, String[] args) {
                Vars.API.sendPrivateMessage(c.getId(), "[URL=" + Messages.get("website") + "]Website[/URL]");
                return CommandResponse.SUCCESS;
            }
        });
        Vars.COMMAND_MGR.registerCommand("skype").setCommandHandler(null, new CommandHandler() {
            @Override
            public CommandResponse handleCommand(ClientInfo c, String[] args) {
                Vars.API.sendPrivateMessage(c.getId(), "Skype ID: " + Messages.get("skype"));
                return CommandResponse.SUCCESS;
            }
        });
        Vars.COMMAND_MGR.registerCommand("login").setCommandHandler(null, new CommandHandler() {
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
        });
        Vars.COMMAND_MGR.registerCommand("support").setCommandHandler(null, new CommandHandler() {
            @Override
            public CommandResponse handleCommand(ClientInfo c, String[] args) {

                Vars.API.moveClient(c.getId(), Vars.SUPPORT_CHANNEL_IDS.get(0));
                return CommandResponse.SUCCESS;
            }
        });
        Vars.COMMAND_MGR.registerCommand("mute").setCommandHandler(null, new CommandHandler() {
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
        });
        Vars.COMMAND_MGR.registerCommand("toggle", "!toggle module|command [*name*|list]").setCommandHandler(null, new CommandHandler() {
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
        });
        Vars.COMMAND_MGR.registerCommand("sendmsg", "!sendmsg private [exact client name] [msg]\n" + "!sendmsg public [msg]").setCommandHandler(null, new CommandHandler() {
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
        });
        Vars.COMMAND_MGR.setCommandPermissionGroup(Vars.COMMAND_MGR.getCommands().get("login"), Vars.PERMGROUPS.get(Vars.PERMGROUPASSIGNMENTS.get("command_login")));
        Vars.COMMAND_MGR.setCommandPermissionGroup(Vars.COMMAND_MGR.getCommands().get("toggle"), Vars.PERMGROUPS.get(Vars.PERMGROUPASSIGNMENTS.get("command_toggle")));
        Vars.COMMAND_MGR.setCommandPermissionGroup(Vars.COMMAND_MGR.getCommands().get("sendmsg"), Vars.PERMGROUPS.get(Vars.PERMGROUPASSIGNMENTS.get("command_sendmsg")));

        for (String command : Vars.DISABLED_CONF_COMMANDS) {
            command = command.substring(1);
            if (!Vars.COMMAND_MGR.getCommands().containsKey(command))
                continue;
            ChatCommand chatCmd = Vars.COMMAND_MGR.getCommands().get(command);
            Vars.COMMAND_MGR.disableCommand(chatCmd, true);
        }

        if (Vars.VPNCHECKER_ENABLED && Vars.VPNCHECKER_SAVE) {
            System.out.println("Loading VPN Checker List...");
            try {
                vpnConfig = new VPNConfig(new File("vpnips.ini"));
            } catch (IOException e) {
                Exceptions.handle(e, "VPN Checker Error", false);
            }
        }

        if (Vars.UPDATE_ENABLED) {
            System.out.println("[Updater] Checking for updates!");
            Updater updater = new Updater();
            try {
                if (updater.isUpdateAvailable()) {
                    System.out.println("[Updater] UPDATE AVAILABLE!");
                    System.out.println("[Updater] Download here: https://sprum.ml/releases/latest");
                    Vars.UPDATE_AVAILABLE = true;
                }
            } catch (Exception updateException) {
                Exceptions.handle(updateException, "UPDATER ERROR", false);
            }
        }
        Vars.SPRUMMLBOT_STATUS = State.STARTING;
        System.out.println("Hello! Sprummlbot v" + Vars.VERSION + " is starting...");
        System.out.println("This Bot is powered by https://github.com/TheHolyWaffle/TeamSpeak-3-Java-API");
        System.out.println("Please put the ip of your bot into your serverquery-whitelist!");
        try {
            Connect.init();
        } catch (Exception connectException) {
            Exceptions.handle(connectException, "Connection Error!");
        }
        if (Vars.IP.equalsIgnoreCase("auto"))
            try {
                Vars.IP = EasyMethods.getPublicIP();
            } catch (IOException e) {
                Exceptions.handle(e, "Couldn't get public ip.");
            }
        if (Vars.DYNBANNER_ENABLED) {
            try {
                System.out.println("Initializing Dynamic Banner...");
                if (!Vars.DYNBANNER_FILE.exists())
                    Exceptions.handle(new FileNotFoundException("Banner file doesnt exist"),
                            "Banner File doesn't exist", true);
                banner = new DynamicBanner(Vars.DYNBANNER_FILE, Vars.DYNBANNER_COLOR,
                        Vars.DYNBANNER_FONT);
                Map<VirtualServerProperty, String> settings = new HashMap<>();
                settings.put(VirtualServerProperty.VIRTUALSERVER_HOSTBANNER_GFX_URL,
                        "http://" + Vars.IP + ":9911/f/banner.png");
                settings.put(VirtualServerProperty.VIRTUALSERVER_HOSTBANNER_GFX_INTERVAL, "60");
                Vars.API.editServer(settings);
            } catch (IOException e) {
                Exceptions.handle(e, "Error while initializing Interactive Banner");
            }
        }
        System.out.println("Initializing webinterface...");
        try {
            FileLoader.unpackDefaults();
        } catch (IOException e) {
            Exceptions.handle(e, "Couldn't unpack default webinterface");
        }
        System.out.println("Starting Webinterface...");
        try {
            WebServerManager.start();
        } catch (IOException e) {
            Exceptions.handle(e, "Webinterface couldn't start");
        }
        System.out.println("Webinterface started! Try \"!login\" with Admin permissions or \"login\" in the console");

        pluginLoader.loadAll();

        Console.runReadThread();
        Tasks.startInternalRunner();
        System.out.println("DONE! It took " + (new DecimalFormat("#.00").format((double) (System.currentTimeMillis() - Main.startTime) / 1000)) + " seconds.");
        System.out.println("Available Commands: stop, login");
    }
}
