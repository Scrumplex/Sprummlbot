package net.scrumplex.sprummlbot.plugins.events;

import com.github.theholywaffle.teamspeak3.api.event.ChannelCreateEvent;

public interface ChannelCreateEventHandler {
    void handleEvent(ChannelCreateEvent event);
}
