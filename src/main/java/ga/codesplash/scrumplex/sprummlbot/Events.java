package ga.codesplash.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.api.CommandFuture;
import com.github.theholywaffle.teamspeak3.api.event.*;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import ga.codesplash.scrumplex.sprummlbot.configurations.Messages;
import ga.codesplash.scrumplex.sprummlbot.plugins.Plugin;
import ga.codesplash.scrumplex.sprummlbot.plugins.SprummlEventType;

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
                                    plugin.getPlugin().handleEvent(SprummlEventType.MESSAGE, e);
                                }
                            }
                        }
                    });
                }
            }

            public void onServerEdit(final ServerEditedEvent e) {
                for (Plugin plugin : Main.pluginManager.getPlugins()) {
                    plugin.getPlugin().handleEvent(SprummlEventType.VIRTUAL_SERVER_EDIT, e);
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

            public void onClientMoved(ClientMovedEvent e) {
                for (Plugin plugin : Main.pluginManager.getPlugins()) {
                    plugin.getPlugin().handleEvent(SprummlEventType.CLIENT_MOVE, e);
                }
            }

            public void onClientLeave(ClientLeaveEvent e) {
                for (Plugin plugin : Main.pluginManager.getPlugins()) {
                    plugin.getPlugin().handleEvent(SprummlEventType.CLIENT_LEAVE, e);
                }
            }

            public void onClientJoin(ClientJoinEvent e) {
                for (Plugin plugin : Main.pluginManager.getPlugins()) {
                    plugin.getPlugin().handleEvent(SprummlEventType.CLIENT_JOIN, e);
                }
                Vars.API.sendPrivateMessage(e.getClientId(), Messages.get("welcome").replace("%client-username%",e.getClientNickname()));
                Vars.API.sendPrivateMessage(e.getClientId(), Messages.get("commandslist").replace("%commands%", Commands.AVAILABLE_COMMANDS));
            }

            public void onChannelEdit(ChannelEditedEvent e) {
                for (Plugin plugin : Main.pluginManager.getPlugins()) {
                    plugin.getPlugin().handleEvent(SprummlEventType.CHANNEL_EDIT, e);
                }
            }

            public void onChannelDescriptionChanged(ChannelDescriptionEditedEvent e) {
                for (Plugin plugin : Main.pluginManager.getPlugins()) {
                    plugin.getPlugin().handleEvent(SprummlEventType.CHANNEL_DESC_CHANGED, e);
                }
            }

            public void onChannelCreate(ChannelCreateEvent e) {
                for (Plugin plugin : Main.pluginManager.getPlugins()) {
                    plugin.getPlugin().handleEvent(SprummlEventType.CHANNEL_CREATE, e);
                }
            }

            public void onChannelDeleted(ChannelDeletedEvent e) {
                for (Plugin plugin : Main.pluginManager.getPlugins()) {
                    plugin.getPlugin().handleEvent(SprummlEventType.CHANNEL_DELETE, e);
                }
            }

            public void onChannelMoved(ChannelMovedEvent e) {
                for (Plugin plugin : Main.pluginManager.getPlugins()) {
                    plugin.getPlugin().handleEvent(SprummlEventType.CHANNEL_MOVE, e);
                }
            }

            public void onChannelPasswordChanged(ChannelPasswordChangedEvent e) {
                for (Plugin plugin : Main.pluginManager.getPlugins()) {
                    plugin.getPlugin().handleEvent(SprummlEventType.CHANNEL_PW_CHANGED, e);
                }
            }
        });
    }
}
