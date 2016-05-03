package net.scrumplex.sprummlbot.plugins.events;

import com.github.theholywaffle.teamspeak3.api.event.ChannelPasswordChangedEvent;

public interface ChannelPasswordChangedEventHandler {
    public void handleEvent(ChannelPasswordChangedEvent event);
}
