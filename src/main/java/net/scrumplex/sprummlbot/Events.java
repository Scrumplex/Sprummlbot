package net.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.api.CommandFuture;
import com.github.theholywaffle.teamspeak3.api.event.*;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import net.scrumplex.sprummlbot.config.Messages;
import net.scrumplex.sprummlbot.plugins.SprummlbotPlugin;
import net.scrumplex.sprummlbot.tools.Exceptions;
import net.scrumplex.sprummlbot.wrapper.CommandResponse;
import net.scrumplex.sprummlbot.wrapper.State;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

class Events {

    private final static List<String> clients = new ArrayList<>();

    static void start() {
        final Sprummlbot sprummlbot = Sprummlbot.getSprummlbot();
        final TS3ApiAsync api = sprummlbot.getDefaultAPI();
        sprummlbot.getSyncAPI().addTS3Listeners(new TS3Listener() {
            public void onTextMessage(final TextMessageEvent e) {
                if (sprummlbot.getSprummlbotState() == State.STOPPING)
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
                        api.getClientInfo(e.getInvokerId()).onSuccess(new CommandFuture.SuccessListener<ClientInfo>() {
                            @Override
                            public void handleSuccess(ClientInfo c) {
                                System.out.println("Processing command " + message + " from " + e.getInvokerName());
                                CommandResponse response = Sprummlbot.getSprummlbot().getCommandManager().handleClientCommand(message, c);
                                switch (response) {
                                    case INTERNAL_ERROR:
                                        api.sendPrivateMessage(e.getInvokerId(), Messages.get("command-error"));
                                        break;
                                    case FORBIDDEN:
                                        api.sendPrivateMessage(e.getInvokerId(), Messages.get("command-no-permission"));
                                        break;
                                    case SYNTAX_ERROR:
                                    case SUCCESS:
                                    case ERROR:
                                        break;
                                    default:
                                        api.sendPrivateMessage(e.getInvokerId(), Messages.get("unknown-command"));
                                        break;
                                }
                            }
                        });
                    } else {
                        try {
                            sprummlbot.getMainEventManager().fireEvent(e);
                        } catch (Exception ex) {
                            Exceptions.handle(ex, "Error while executing event handlers", false);
                        }
                        for (SprummlbotPlugin plugin : Sprummlbot.getSprummlbot().getPluginManager().getPlugins()) {
                            try {
                                plugin.getEventManager().fireEvent(e);
                            } catch (Exception ex) {
                                Exceptions.handlePluginError(ex, plugin);
                            }
                        }
                    }
                }
            }

            public void onServerEdit(final ServerEditedEvent e) {
                if (sprummlbot.getSprummlbotState() == State.STOPPING)
                    return;
                try {
                    sprummlbot.getMainEventManager().fireEvent(e);
                } catch (Exception ex) {
                    Exceptions.handle(ex, "Error while executing event handlers", false);
                }
                for (SprummlbotPlugin plugin : Sprummlbot.getSprummlbot().getPluginManager().getPlugins()) {
                    try {
                        plugin.getEventManager().fireEvent(e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }

                if (!e.getInvokerUniqueId().equalsIgnoreCase("serveradmin")) {
                    api.getClientInfo(e.getInvokerId()).onSuccess(new CommandFuture.SuccessListener<ClientInfo>() {
                        @Override
                        public void handleSuccess(ClientInfo cl) {
                            System.out.println("The user " + e.getInvokerName() + " edited the Server! User info: uid="
                                    + cl.getUniqueIdentifier() + " ip=" + cl.getIp() + " country=" + cl.getCountry() + ".");
                        }
                    });
                }
            }

            public void onClientMoved(final ClientMovedEvent e) {
                if (sprummlbot.getSprummlbotState() == State.STOPPING)
                    return;
                try {
                    sprummlbot.getMainEventManager().fireEvent(e);
                } catch (Exception ex) {
                    Exceptions.handle(ex, "Error while executing event handlers", false);
                }
                for (SprummlbotPlugin plugin : Sprummlbot.getSprummlbot().getPluginManager().getPlugins()) {
                    try {
                        plugin.getEventManager().fireEvent(e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
            }

            public void onClientLeave(final ClientLeaveEvent e) {
                if (sprummlbot.getSprummlbotState() == State.STOPPING)
                    return;
                try {
                    sprummlbot.getMainEventManager().fireEvent(e);
                } catch (Exception ex) {
                    Exceptions.handle(ex, "Error while executing event handlers", false);
                }
                for (SprummlbotPlugin plugin : Sprummlbot.getSprummlbot().getPluginManager().getPlugins()) {
                    try {
                        plugin.getEventManager().fireEvent(e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
            }

            public void onClientJoin(final ClientJoinEvent e) {
                if (sprummlbot.getSprummlbotState() == State.STOPPING)
                    return;
                try {
                    sprummlbot.getMainEventManager().fireEvent(e);
                } catch (Exception ex) {
                    Exceptions.handle(ex, "Error while executing event handlers", false);
                }
                for (SprummlbotPlugin plugin : Sprummlbot.getSprummlbot().getPluginManager().getPlugins()) {
                    try {
                        plugin.getEventManager().fireEvent(e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
            }

            public void onChannelEdit(final ChannelEditedEvent e) {
                if (sprummlbot.getSprummlbotState() == State.STOPPING)
                    return;
                try {
                    sprummlbot.getMainEventManager().fireEvent(e);
                } catch (Exception ex) {
                    Exceptions.handle(ex, "Error while executing event handlers", false);
                }
                for (SprummlbotPlugin plugin : Sprummlbot.getSprummlbot().getPluginManager().getPlugins()) {
                    try {
                        plugin.getEventManager().fireEvent(e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
            }

            public void onChannelDescriptionChanged(final ChannelDescriptionEditedEvent e) {
                if (sprummlbot.getSprummlbotState() == State.STOPPING)
                    return;
                try {
                    sprummlbot.getMainEventManager().fireEvent(e);
                } catch (Exception ex) {
                    Exceptions.handle(ex, "Error while executing event handlers", false);
                }
                for (SprummlbotPlugin plugin : Sprummlbot.getSprummlbot().getPluginManager().getPlugins()) {
                    try {
                        plugin.getEventManager().fireEvent(e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
            }

            public void onChannelCreate(final ChannelCreateEvent e) {
                if (sprummlbot.getSprummlbotState() == State.STOPPING)
                    return;
                try {
                    sprummlbot.getMainEventManager().fireEvent(e);
                } catch (Exception ex) {
                    Exceptions.handle(ex, "Error while executing event handlers", false);
                }
                for (SprummlbotPlugin plugin : Sprummlbot.getSprummlbot().getPluginManager().getPlugins()) {
                    try {
                        plugin.getEventManager().fireEvent(e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
            }

            public void onChannelDeleted(final ChannelDeletedEvent e) {
                if (sprummlbot.getSprummlbotState() == State.STOPPING)
                    return;
                try {
                    sprummlbot.getMainEventManager().fireEvent(e);
                } catch (Exception ex) {
                    Exceptions.handle(ex, "Error while executing event handlers", false);
                }
                for (SprummlbotPlugin plugin : Sprummlbot.getSprummlbot().getPluginManager().getPlugins()) {
                    try {
                        plugin.getEventManager().fireEvent(e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
            }

            public void onChannelMoved(final ChannelMovedEvent e) {
                if (sprummlbot.getSprummlbotState() == State.STOPPING)
                    return;
                try {
                    sprummlbot.getMainEventManager().fireEvent(e);
                } catch (Exception ex) {
                    Exceptions.handle(ex, "Error while executing event handlers", false);
                }
                for (SprummlbotPlugin plugin : Sprummlbot.getSprummlbot().getPluginManager().getPlugins()) {
                    try {
                        plugin.getEventManager().fireEvent(e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
            }

            public void onChannelPasswordChanged(final ChannelPasswordChangedEvent e) {
                if (sprummlbot.getSprummlbotState() == State.STOPPING)
                    return;
                try {
                    sprummlbot.getMainEventManager().fireEvent(e);
                } catch (Exception ex) {
                    Exceptions.handle(ex, "Error while executing event handlers", false);
                }
                for (SprummlbotPlugin plugin : Sprummlbot.getSprummlbot().getPluginManager().getPlugins()) {
                    try {
                        plugin.getEventManager().fireEvent(e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
            }

            public void onPrivilegeKeyUsed(PrivilegeKeyUsedEvent e) {
                if (sprummlbot.getSprummlbotState() == State.STOPPING)
                    return;
                try {
                    sprummlbot.getMainEventManager().fireEvent(e);
                } catch (Exception ex) {
                    Exceptions.handle(ex, "Error while executing event handlers", false);
                }
                for (SprummlbotPlugin plugin : Sprummlbot.getSprummlbot().getPluginManager().getPlugins()) {
                    try {
                        plugin.getEventManager().fireEvent(e);
                    } catch (Exception ex) {
                        Exceptions.handlePluginError(ex, plugin);
                    }
                }
            }
        });
    }

}
