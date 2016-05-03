package net.scrumplex.sprummlbot.plugins.events;

import com.github.theholywaffle.teamspeak3.api.event.ChannelDeletedEvent;

public interface ChannelDeleteEventHandler {
    public void handleEvent(ChannelDeletedEvent event);
}
