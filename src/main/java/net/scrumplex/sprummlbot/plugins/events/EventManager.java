package net.scrumplex.sprummlbot.plugins.events;

import com.github.theholywaffle.teamspeak3.api.event.*;
import net.scrumplex.sprummlbot.plugins.SprummlbotPlugin;
import net.scrumplex.sprummlbot.wrapper.State;

import java.util.ArrayList;
import java.util.List;

public class EventManager {

    private final SprummlbotPlugin plugin;
    private final List<ChannelCreateEventHandler> channelCreateEvents = new ArrayList<>();
    private final List<ChannelEditEventHandler> channelEditEvents = new ArrayList<>();
    private final List<ChannelDeleteEventHandler> channelDeleteEvents = new ArrayList<>();
    private final List<ChannelDescriptionEditEventHandler> channelDescChangeEvents = new ArrayList<>();
    private final List<ChannelMovedEventHandler> channelMoveEvents = new ArrayList<>();
    private final List<ChannelPasswordChangedEventHandler> channelPasswordChangeEvents = new ArrayList<>();
    private final List<ClientJoinEventHandler> joinEvents = new ArrayList<>();
    private final List<ClientQuitEventHandler> quitEvents = new ArrayList<>();
    private final List<ClientMoveEventHandler> movedEvents = new ArrayList<>();
    private final List<ServerEditEventHandler> serverEditEvents = new ArrayList<>();
    private final List<TextMessageEventHandler> textMessageEvents = new ArrayList<>();
    private final List<ClientPrivilegeKeyUsedEventHandler> privilegeKeyUsedEvents = new ArrayList<>();

    public EventManager(SprummlbotPlugin plugin) {
        this.plugin = plugin;
    }

    private boolean preprocessEvent() {
        return plugin.getSprummlbotState() != State.STOPPING;
    }

    public void handleEvent(ChannelDescriptionEditedEvent event) {
        if (!preprocessEvent())
            return;

        for (ChannelDescriptionEditEventHandler handler : channelDescChangeEvents) {
            handler.handleEvent(event);
        }
    }

    public void handleEvent(PrivilegeKeyUsedEvent event) {
        if (!preprocessEvent())
            return;

        for (ClientPrivilegeKeyUsedEventHandler handler : privilegeKeyUsedEvents) {
            handler.handleEvent(event);
        }
    }

    public void handleEvent(ChannelMovedEvent event) {
        if (!preprocessEvent())
            return;

        for (ChannelMovedEventHandler handler : channelMoveEvents) {
            handler.handleEvent(event);
        }
    }

    public void handleEvent(ChannelPasswordChangedEvent event) {
        if (!preprocessEvent())
            return;

        for (ChannelPasswordChangedEventHandler handler : channelPasswordChangeEvents) {
            handler.handleEvent(event);
        }
    }

    public void handleEvent(ChannelCreateEvent event) {
        if (!preprocessEvent())
            return;

        for (ChannelCreateEventHandler handler : channelCreateEvents) {
            handler.handleEvent(event);
        }
    }

    public void handleEvent(ChannelEditedEvent event) {
        if (!preprocessEvent())
            return;

        for (ChannelEditEventHandler handler : channelEditEvents) {
            handler.handleEvent(event);
        }
    }

    public void handleEvent(ChannelDeletedEvent event) {
        if (!preprocessEvent())
            return;

        for (ChannelDeleteEventHandler handler : channelDeleteEvents) {
            handler.handleEvent(event);
        }
    }

    public void handleEvent(ClientJoinEvent event) {
        if (!preprocessEvent())
            return;

        for (ClientJoinEventHandler handler : joinEvents) {
            handler.handleEvent(event);
        }
    }

    public void handleEvent(ClientLeaveEvent event) {
        if (!preprocessEvent())
            return;

        for (ClientQuitEventHandler handler : quitEvents) {
            handler.handleEvent(event);
        }
    }

    public void handleEvent(ServerEditedEvent event) {
        if (!preprocessEvent())
            return;

        for (ServerEditEventHandler handler : serverEditEvents) {
            handler.handleEvent(event);
        }
    }

    public void handleEvent(ClientMovedEvent event) {
        if (!preprocessEvent())
            return;

        for (ClientMoveEventHandler handler : movedEvents) {
            handler.handleEvent(event);
        }
    }

    public void handleEvent(TextMessageEvent event) {
        if (!preprocessEvent())
            return;

        for (TextMessageEventHandler handler : textMessageEvents) {
            handler.handleEvent(event);
        }
    }

    public void addEventListener(ChannelDescriptionEditEventHandler handler) {
        channelDescChangeEvents.add(handler);
    }

    public void addEventListener(ClientPrivilegeKeyUsedEventHandler handler) {
        privilegeKeyUsedEvents.add(handler);
    }

    public void addEventListener(ChannelMovedEventHandler handler) {
        channelMoveEvents.add(handler);
    }

    public void addEventListener(ChannelPasswordChangedEventHandler handler) {
        channelPasswordChangeEvents.add(handler);
    }

    public void addEventListener(ChannelCreateEventHandler handler) {
        channelCreateEvents.add(handler);
    }

    public void addEventListener(ChannelEditEventHandler handler) {
        channelEditEvents.add(handler);
    }

    public void addEventListener(ChannelDeleteEventHandler handler) {
        channelDeleteEvents.add(handler);
    }

    public void addEventListener(ClientJoinEventHandler handler) {
        joinEvents.add(handler);
    }

    public void addEventListener(ClientQuitEventHandler handler) {
        quitEvents.add(handler);
    }

    public void addEventListener(ServerEditEventHandler handler) {
        serverEditEvents.add(handler);
    }

    public void addEventListener(ClientMoveEventHandler handler) {
        movedEvents.add(handler);
    }

    public void addEventListener(TextMessageEventHandler handler) {
        textMessageEvents.add(handler);
    }

}
