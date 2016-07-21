package net.scrumplex.sprummlbot.plugins.events;

import com.github.theholywaffle.teamspeak3.api.event.*;
import net.scrumplex.sprummlbot.plugins.SprummlbotPlugin;
import net.scrumplex.sprummlbot.wrapper.State;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EventManager {

    private final SprummlbotPlugin plugin;
    private final Map<Integer, ChannelCreateEventHandler> channelCreateEvents = new HashMap<>();
    private final Map<Integer, ChannelEditEventHandler> channelEditEvents = new HashMap<>();
    private final Map<Integer, ChannelDeleteEventHandler> channelDeleteEvents = new HashMap<>();
    private final Map<Integer, ChannelDescriptionEditEventHandler> channelDescChangeEvents = new HashMap<>();
    private final Map<Integer, ChannelMovedEventHandler> channelMoveEvents = new HashMap<>();
    private final Map<Integer, ChannelPasswordChangedEventHandler> channelPasswordChangeEvents = new HashMap<>();
    private final Map<Integer, ClientJoinEventHandler> joinEvents = new HashMap<>();
    private final Map<Integer, ClientQuitEventHandler> quitEvents = new HashMap<>();
    private final Map<Integer, ClientMoveEventHandler> movedEvents = new HashMap<>();
    private final Map<Integer, ServerEditEventHandler> serverEditEvents = new HashMap<>();
    private final Map<Integer, TextMessageEventHandler> textMessageEvents = new HashMap<>();
    private final Map<Integer, ClientPrivilegeKeyUsedEventHandler> privilegeKeyUsedEvents = new HashMap<>();

    public EventManager(SprummlbotPlugin plugin) {
        this.plugin = plugin;
    }

    private boolean preprocessEvent() {
        return plugin == null || plugin.getSprummlbotState() != State.STOPPING;
    }

    public void fireEvent(ChannelDescriptionEditedEvent event) {
        if (!preprocessEvent())
            return;

        for (ChannelDescriptionEditEventHandler handler : channelDescChangeEvents.values()) {
            handler.handleEvent(event);
        }
    }

    public void fireEvent(PrivilegeKeyUsedEvent event) {
        if (!preprocessEvent())
            return;

        for (ClientPrivilegeKeyUsedEventHandler handler : privilegeKeyUsedEvents.values()) {
            handler.handleEvent(event);
        }
    }

    public void fireEvent(ChannelMovedEvent event) {
        if (!preprocessEvent())
            return;

        for (ChannelMovedEventHandler handler : channelMoveEvents.values()) {
            handler.handleEvent(event);
        }
    }

    public void fireEvent(ChannelPasswordChangedEvent event) {
        if (!preprocessEvent())
            return;

        for (ChannelPasswordChangedEventHandler handler : channelPasswordChangeEvents.values()) {
            handler.handleEvent(event);
        }
    }

    public void fireEvent(ChannelCreateEvent event) {
        if (!preprocessEvent())
            return;

        for (ChannelCreateEventHandler handler : channelCreateEvents.values()) {
            handler.handleEvent(event);
        }
    }

    public void fireEvent(ChannelEditedEvent event) {
        if (!preprocessEvent())
            return;

        for (ChannelEditEventHandler handler : channelEditEvents.values()) {
            handler.handleEvent(event);
        }
    }

    public void fireEvent(ChannelDeletedEvent event) {
        if (!preprocessEvent())
            return;

        for (ChannelDeleteEventHandler handler : channelDeleteEvents.values()) {
            handler.handleEvent(event);
        }
    }

    public void fireEvent(ClientJoinEvent event) {
        if (!preprocessEvent())
            return;

        for (ClientJoinEventHandler handler : joinEvents.values()) {
            handler.handleEvent(event);
        }
    }

    public void fireEvent(ClientLeaveEvent event) {
        if (!preprocessEvent())
            return;

        for (ClientQuitEventHandler handler : quitEvents.values()) {
            handler.handleEvent(event);
        }
    }

    public void fireEvent(ServerEditedEvent event) {
        if (!preprocessEvent())
            return;

        for (ServerEditEventHandler handler : serverEditEvents.values()) {
            handler.handleEvent(event);
        }
    }

    public void fireEvent(ClientMovedEvent event) {
        if (!preprocessEvent())
            return;

        for (ClientMoveEventHandler handler : movedEvents.values()) {
            handler.handleEvent(event);
        }
    }

    public void fireEvent(TextMessageEvent event) {
        if (!preprocessEvent())
            return;

        for (TextMessageEventHandler handler : textMessageEvents.values()) {
            handler.handleEvent(event);
        }
    }

    public int addEventListener(ChannelDescriptionEditEventHandler handler) {
        int id = getRandomID();
        channelDescChangeEvents.put(id, handler);
        return id;
    }

    public int addEventListener(ClientPrivilegeKeyUsedEventHandler handler) {
        int id = getRandomID();
        privilegeKeyUsedEvents.put(id, handler);
        return id;
    }

    public int addEventListener(ChannelMovedEventHandler handler) {
        int id = getRandomID();
        channelMoveEvents.put(id, handler);
        return id;
    }

    public int addEventListener(ChannelPasswordChangedEventHandler handler) {
        int id = getRandomID();
        channelPasswordChangeEvents.put(id, handler);
        return id;
    }

    public int addEventListener(ChannelCreateEventHandler handler) {
        int id = getRandomID();
        channelCreateEvents.put(id, handler);
        return id;
    }

    public int addEventListener(ChannelEditEventHandler handler) {
        int id = getRandomID();
        channelEditEvents.put(id, handler);
        return id;
    }

    public int addEventListener(ChannelDeleteEventHandler handler) {
        int id = getRandomID();
        channelDeleteEvents.put(id, handler);
        return id;
    }

    public int addEventListener(ClientJoinEventHandler handler) {
        int id = getRandomID();
        joinEvents.put(id, handler);
        return id;
    }

    public int addEventListener(ClientQuitEventHandler handler) {
        int id = getRandomID();
        quitEvents.put(id, handler);
        return id;
    }

    public int addEventListener(ServerEditEventHandler handler) {
        int id = getRandomID();
        serverEditEvents.put(id, handler);
        return id;
    }

    public int addEventListener(ClientMoveEventHandler handler) {
        int id = getRandomID();
        movedEvents.put(id, handler);
        return id;
    }

    public int addEventListener(TextMessageEventHandler handler) {
        int id = getRandomID();
        textMessageEvents.put(id, handler);
        return id;
    }

    public void removeEventListener(int id) {
        channelCreateEvents.remove(id);
        channelEditEvents.remove(id);
        channelDeleteEvents.remove(id);
        channelDescChangeEvents.remove(id);
        channelMoveEvents.remove(id);
        channelPasswordChangeEvents.remove(id);
        joinEvents.remove(id);
        quitEvents.remove(id);
        movedEvents.remove(id);
        serverEditEvents.remove(id);
        textMessageEvents.remove(id);
        privilegeKeyUsedEvents.remove(id);
    }

    private int getRandomID() {
        Random r = new Random();
        int i = r.nextInt(10000);
        while (channelCreateEvents.containsKey(i) || channelEditEvents.containsKey(i) || channelDeleteEvents.containsKey(i) || channelDescChangeEvents.containsKey(i) || channelMoveEvents.containsKey(i) || channelPasswordChangeEvents.containsKey(i) || joinEvents.containsKey(i) || quitEvents.containsKey(i) || movedEvents.containsKey(i) || serverEditEvents.containsKey(i) || textMessageEvents.containsKey(i) || privilegeKeyUsedEvents.containsKey(i)) {
            i = r.nextInt(10000);
        }
        return i;
    }

}
