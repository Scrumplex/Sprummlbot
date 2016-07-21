package net.scrumplex.sprummlbot.plugins.events;

import com.github.theholywaffle.teamspeak3.api.event.ChannelEditedEvent;

public interface ChannelEditEventHandler {
    void handleEvent(ChannelEditedEvent event);
}
