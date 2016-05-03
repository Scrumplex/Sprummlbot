package net.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.api.CommandFuture;
import com.github.theholywaffle.teamspeak3.api.event.*;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import net.scrumplex.sprummlbot.configurations.Messages;
import net.scrumplex.sprummlbot.plugins.SprummlEventType;
import net.scrumplex.sprummlbot.plugins.SprummlbotPlugin;
import net.scrumplex.sprummlbot.tools.Exceptions;
import net.scrumplex.sprummlbot.vpn.VPNChecker;
import net.scrumplex.sprummlbot.wrapper.CommandResponse;
import net.scrumplex.sprummlbot.wrapper.State;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static net.scrumplex.sprummlbot.Vars.API;

class Events {

    private final static List<String> clients = new ArrayList<>();

    static void start() {
        API.addTS3Listeners(new TS3Listener() {
            public void onTextMessage(final TextMessageEvent e) {
                if (Vars.SPRUMMLBOT_STATUS == State.STOPPING)
                    return;
                if (!e.getInvokerUniqueId().equalsIgnoreCase("serveradmin")) {
                    if (!clients.contains(e.getInvokerUniqueId())) {
                        clients.add(e.getInvokerUniqueId());
                        Vars.SERVICE.schedule(new Runnable() {
                            @Override
                            public void run() {
                                clients.remove(e.getInvokerUniqueId());
                            }
                        }, 500, TimeUnit.MILLISECONDS);
                    } else
                        return;
                    String msg = e.getMessage();
                    while (msg.endsWith(" ")) {
                        msg = msg.substring(0, msg.length() - 1);
                    }
                    final String message = msg;
                    if (message.startsWith("!")) {
                        API.getClientInfo(e.getInvokerId()).onSuccess(new CommandFuture.SuccessListener<ClientInfo>() {
                            @Override
                            public void handleSuccess(ClientInfo c) {
                                System.out.println("Processing command " + message + " from " + e.getInvokerName());
                                CommandResponse response = Vars.COMMAND_MGR.handleClientCommand(message, c);
                                switch (response) {
                                    case INTERNAL_ERROR:
                                        API.sendPrivateMessage(e.getInvokerId(), Messages.get("command-error"));
                                        break;
                                    case FORBIDDEN:
                                        API.sendPrivateMessage(e.getInvokerId(), Messages.get("command-no-permission"));
                                        break;
                                    case SYNTAX_ERROR:
                                    case SUCCESS:
                                    case ERROR:
                                        break;
                                    default:
                                        API.sendPrivateMessage(e.getInvokerId(), Messages.get("unknown-command"));
                                        break;
                                }
                            }
                        });
                    } else {
                        for (SprummlbotPlugin plugin : Startup.pluginManager.getPlugins()) {
                            try {
                                plugin.getEventManager().handleEvent(e);
                                plugin.onEvent(SprummlEventType.MESSAGE, e);
                            } catch (Exception ex) {
                                Exceptions.handlePluginError(ex, plugin);
                            }
                        }
                    }
                }
            }

            public void onServerEdit(final ServerEditedEvent e) {
                if (Vars.SPRUMMLBOT_STATUS == State.STOPPING)
                    return;
                for (SprummlbotPlugin plugin : Startup.pluginManager.getPlugins()) {
                    try {
                        plugin.getEventManager().handleEvent(e);
                        plugin.onEvent(SprummlEventType.VIRTUAL_SERVER_EDIT, e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }

                if (!e.getInvokerUniqueId().equalsIgnoreCase("serveradmin")) {
                    API.getClientInfo(e.getInvokerId()).onSuccess(new CommandFuture.SuccessListener<ClientInfo>() {
                        @Override
                        public void handleSuccess(ClientInfo cl) {
                            System.out.println("The user " + e.getInvokerName() + " edited the Server! User info: uid="
                                    + cl.getUniqueIdentifier() + " ip=" + cl.getIp() + " country=" + cl.getCountry() + ".");
                        }
                    });
                }
            }

            public void onClientMoved(final ClientMovedEvent e) {
                if (Vars.SPRUMMLBOT_STATUS == State.STOPPING)
                    return;
                for (SprummlbotPlugin plugin : Startup.pluginManager.getPlugins()) {
                    try {
                        plugin.getEventManager().handleEvent(e);
                        plugin.onEvent(SprummlEventType.CLIENT_MOVE, e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }

                ClientInfo c = API.getClientInfo(e.getClientId()).getUninterruptibly();
                if (Vars.IN_AFK.containsKey(e.getClientId())) {
                    if (e.getTargetChannelId() != Vars.AFK_CHANNEL_ID) {
                        Vars.IN_AFK.remove(e.getClientId());
                        System.out.println("[AFK Mover] " + c.getNickname() + "(" + c.getId() + ") left afk channel.");
                    }
                }

                if (Vars.SUPPORT_ENABLED) {
                    if (Vars.SUPPORT_CHANNEL_IDS.contains(e.getTargetChannelId()))
                        if (!Vars.IN_SUPPORT.contains(c.getId())) {
                            API.sendPrivateMessage(c.getId(), Messages.get("you-joined-support-channel"));
                            Vars.IN_SUPPORT.add(c.getId());
                            System.out.println("[Support] " + c.getNickname() + " entered a support channel.");
                            API.getClients().onSuccess(new CommandFuture.SuccessListener<List<Client>>() {
                                @Override
                                public void handleSuccess(List<Client> result) {
                                    for (Client user : result) {
                                        if (Vars.PERMGROUPS.get(Vars.PERMGROUPASSIGNMENTS.get("supporters")).isClientInGroup(user.getUniqueIdentifier())) {
                                            if (Vars.SUPPORT_POKE)
                                                API.pokeClient(user.getId(), Messages.get("someone-is-in-support"));
                                            else
                                                API.sendPrivateMessage(user.getId(), Messages.get("someone-is-in-support"));
                                        }
                                    }
                                }
                            });
                        }

                    if (!Vars.SUPPORT_CHANNEL_IDS.contains(e.getTargetChannelId()))
                        if (Vars.IN_SUPPORT.contains(c.getId())) {
                            API.sendPrivateMessage(c.getId(), Messages.get("you-are-not-longer-in-support-queue"));
                            Vars.IN_SUPPORT.remove(new Integer(c.getId()));
                            System.out.println("[Support] " + c.getNickname() + " left support channel.");
                        }
                }
            }

            public void onClientLeave(final ClientLeaveEvent e) {
                if (Vars.SPRUMMLBOT_STATUS == State.STOPPING)
                    return;
                for (SprummlbotPlugin plugin : Startup.pluginManager.getPlugins()) {
                    try {
                        plugin.getEventManager().handleEvent(e);
                        plugin.onEvent(SprummlEventType.CLIENT_LEAVE, e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
                if (Vars.IN_AFK.containsKey(e.getClientId()))
                    Vars.IN_AFK.remove(e.getClientId());

                if (Vars.IN_SUPPORT.contains(e.getClientId()))
                    Vars.IN_SUPPORT.remove(new Integer(e.getClientId()));
            }

            public void onClientJoin(final ClientJoinEvent e) {
                if (Vars.SPRUMMLBOT_STATUS == State.STOPPING)
                    return;
                for (SprummlbotPlugin plugin : Startup.pluginManager.getPlugins()) {
                    try {
                        plugin.getEventManager().handleEvent(e);
                        plugin.onEvent(SprummlEventType.CLIENT_JOIN, e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
                if (Vars.IN_AFK.containsKey(e.getClientId()))
                    Vars.IN_AFK.remove(e.getClientId());

                if (Vars.IN_SUPPORT.contains(e.getClientId()))
                    Vars.IN_SUPPORT.remove(new Integer(e.getClientId()));

                if (Vars.WELCOME_MSG) {
                    API.sendPrivateMessage(e.getClientId(), Messages.get("welcome").replace("%client-username%", e.getClientNickname()));
                    API.sendPrivateMessage(e.getClientId(), Messages.get("commandslist").replace("%commands%", Vars.COMMAND_MGR.buildHelpMessage(e.getUniqueClientIdentifier())));
                }
                API.getClientInfo(e.getClientId()).onSuccess(new CommandFuture.SuccessListener<ClientInfo>() {
                    @Override
                    public void handleSuccess(ClientInfo result) {
                        VPNChecker check = new VPNChecker(result);
                        if (check.isBlocked()) {
                            System.out.println("[VPN Checker] " + result.getNickname() + " was kicked. Blacklisted IP: " + result.getIp());
                            API.kickClientFromServer(Messages.get("you-are-using-vpn"), result.getId());
                        }
                    }
                });
            }

            public void onChannelEdit(final ChannelEditedEvent e) {
                if (Vars.SPRUMMLBOT_STATUS == State.STOPPING)
                    return;
                for (SprummlbotPlugin plugin : Startup.pluginManager.getPlugins()) {
                    try {
                        plugin.getEventManager().handleEvent(e);
                        plugin.onEvent(SprummlEventType.CHANNEL_EDIT, e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
            }

            public void onChannelDescriptionChanged(final ChannelDescriptionEditedEvent e) {
                if (Vars.SPRUMMLBOT_STATUS == State.STOPPING)
                    return;
                for (SprummlbotPlugin plugin : Startup.pluginManager.getPlugins()) {
                    try {
                        plugin.getEventManager().handleEvent(e);
                        plugin.onEvent(SprummlEventType.CHANNEL_DESC_CHANGED, e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
            }

            public void onChannelCreate(final ChannelCreateEvent e) {
                if (Vars.SPRUMMLBOT_STATUS == State.STOPPING)
                    return;
                for (SprummlbotPlugin plugin : Startup.pluginManager.getPlugins()) {
                    try {
                        plugin.getEventManager().handleEvent(e);
                        plugin.onEvent(SprummlEventType.CHANNEL_CREATE, e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
            }

            public void onChannelDeleted(final ChannelDeletedEvent e) {
                if (Vars.SPRUMMLBOT_STATUS == State.STOPPING)
                    return;
                for (SprummlbotPlugin plugin : Startup.pluginManager.getPlugins()) {
                    try {
                        plugin.getEventManager().handleEvent(e);
                        plugin.onEvent(SprummlEventType.CHANNEL_DELETE, e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
            }

            public void onChannelMoved(final ChannelMovedEvent e) {
                if (Vars.SPRUMMLBOT_STATUS == State.STOPPING)
                    return;
                for (SprummlbotPlugin plugin : Startup.pluginManager.getPlugins()) {
                    try {
                        plugin.getEventManager().handleEvent(e);
                        plugin.onEvent(SprummlEventType.CHANNEL_MOVE, e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
            }

            public void onChannelPasswordChanged(final ChannelPasswordChangedEvent e) {
                if (Vars.SPRUMMLBOT_STATUS == State.STOPPING)
                    return;
                for (SprummlbotPlugin plugin : Startup.pluginManager.getPlugins()) {
                    try {
                        plugin.getEventManager().handleEvent(e);
                        plugin.onEvent(SprummlEventType.CHANNEL_PW_CHANGED, e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
            }

            public void onPrivilegeKeyUsed(PrivilegeKeyUsedEvent e) {
                if (Vars.SPRUMMLBOT_STATUS == State.STOPPING)
                    return;
                for (SprummlbotPlugin plugin : Startup.pluginManager.getPlugins()) {
                    try {
                        plugin.getEventManager().handleEvent(e);
                        plugin.onEvent(SprummlEventType.PRIVILEGE_KEY_USED, e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
            }
        });
    }

}
