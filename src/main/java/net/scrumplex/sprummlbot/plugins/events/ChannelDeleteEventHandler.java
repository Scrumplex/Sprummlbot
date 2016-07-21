package net.scrumplex.sprummlbot.plugins.events;

import com.github.theholywaffle.teamspeak3.api.event.ChannelDeletedEvent;

public interface ChannelDeleteEventHandler {
    void handleEvent(ChannelDeletedEvent event);
}
