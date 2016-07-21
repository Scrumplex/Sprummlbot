package net.scrumplex.sprummlbot.plugins.events;

import com.github.theholywaffle.teamspeak3.api.event.ChannelMovedEvent;

public interface ChannelMovedEventHandler {
    void handleEvent(ChannelMovedEvent event);
}
