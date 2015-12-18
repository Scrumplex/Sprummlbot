package ga.codesplash.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.api.CommandFuture;
import com.github.theholywaffle.teamspeak3.api.event.*;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import ga.codesplash.scrumplex.sprummlbot.configurations.Messages;
import ga.codesplash.scrumplex.sprummlbot.plugins.Plugin;
import ga.codesplash.scrumplex.sprummlbot.plugins.SprummlEventType;
import ga.codesplash.scrumplex.sprummlbot.tools.Exceptions;

/**
 * This class handles the events.
 */
class Events {

    /**
     * Registers the events
     */
    public static void start() {
        Vars.API.registerAllEvents();
        Vars.API.addTS3Listeners(new TS3Listener() {
            public void onTextMessage(final TextMessageEvent e) {

                if (e.getInvokerId() != Vars.QID) {
                    final String message = e.getMessage().toLowerCase().replace("<video", "");
                    Vars.API.getClientInfo(e.getInvokerId()).onSuccess(new CommandFuture.SuccessListener<ClientInfo>() {
                        @Override
                        public void handleSuccess(ClientInfo c) {
                            if (message.startsWith("!")) {
                                if (!Commands.handle(message, c)) {
                                    Vars.API.sendPrivateMessage(c.getId(), Messages.get("unknown-command"));
                                }
                                System.out.println(message + " received from " + e.getInvokerName());
                            } else {
                                for (Plugin plugin : Main.pluginManager.getPlugins()) {
                                    try {
                                        plugin.getPlugin().handleEvent(SprummlEventType.MESSAGE, e);
                                    } catch (Exception ex) {
                                        Exceptions.handlePluginError(ex, plugin);
                                    }
                                }
                            }
                        }
                    });
                }
            }

            public void onServerEdit(final ServerEditedEvent e) {
                for (Plugin plugin : Main.pluginManager.getPlugins()) {
                    try {
                        plugin.getPlugin().handleEvent(SprummlEventType.VIRTUAL_SERVER_EDIT, e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }

                if (Vars.QID != e.getInvokerId()) {
                    Vars.API.getClientInfo(e.getInvokerId()).onSuccess(new CommandFuture.SuccessListener<ClientInfo>() {
                        @Override
                        public void handleSuccess(ClientInfo cl) {
                            System.out.println("The user " + e.getInvokerName() + " edited the Server! User info: uid="
                                    + cl.getUniqueIdentifier() + " ip=" + cl.getIp() + " country=" + cl.getCountry() + ".");
                        }
                    });
                }
            }

            public void onClientMoved(final ClientMovedEvent e) {
                for (Plugin plugin : Main.pluginManager.getPlugins()) {
                    try {
                        plugin.getPlugin().handleEvent(SprummlEventType.CLIENT_MOVE, e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
            }

            public void onClientLeave(final ClientLeaveEvent e) {
                for (Plugin plugin : Main.pluginManager.getPlugins()) {
                    try {
                        plugin.getPlugin().handleEvent(SprummlEventType.CLIENT_LEAVE, e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
            }

            public void onClientJoin(final ClientJoinEvent e) {
                for (Plugin plugin : Main.pluginManager.getPlugins()) {
                    try {
                        plugin.getPlugin().handleEvent(SprummlEventType.CLIENT_JOIN, e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
                Vars.API.sendPrivateMessage(e.getClientId(), Messages.get("welcome").replace("%client-username%", e.getClientNickname()));
                Vars.API.sendPrivateMessage(e.getClientId(), Messages.get("commandslist").replace("%commands%", Commands.AVAILABLE_COMMANDS));
            }

            public void onChannelEdit(final ChannelEditedEvent e) {
                for (Plugin plugin : Main.pluginManager.getPlugins()) {
                    try {
                        plugin.getPlugin().handleEvent(SprummlEventType.CHANNEL_EDIT, e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
            }

            public void onChannelDescriptionChanged(final ChannelDescriptionEditedEvent e) {
                for (Plugin plugin : Main.pluginManager.getPlugins()) {
                    try {
                        plugin.getPlugin().handleEvent(SprummlEventType.CHANNEL_DESC_CHANGED, e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
            }

            public void onChannelCreate(final ChannelCreateEvent e) {
                for (Plugin plugin : Main.pluginManager.getPlugins()) {
                    try {
                        plugin.getPlugin().handleEvent(SprummlEventType.CHANNEL_CREATE, e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
            }

            public void onChannelDeleted(final ChannelDeletedEvent e) {
                for (Plugin plugin : Main.pluginManager.getPlugins()) {
                    try {
                        plugin.getPlugin().handleEvent(SprummlEventType.CHANNEL_DELETE, e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
            }

            public void onChannelMoved(final ChannelMovedEvent e) {
                for (Plugin plugin : Main.pluginManager.getPlugins()) {
                    try {
                        plugin.getPlugin().handleEvent(SprummlEventType.CHANNEL_MOVE, e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
            }

            public void onChannelPasswordChanged(final ChannelPasswordChangedEvent e) {
                for (Plugin plugin : Main.pluginManager.getPlugins()) {
                    try {
                        plugin.getPlugin().handleEvent(SprummlEventType.CHANNEL_PW_CHANGED, e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
            }
        });
    }
}
