package net.scrumplex.sprummlbot.plugins.events;

import com.github.theholywaffle.teamspeak3.api.event.ChannelDescriptionEditedEvent;

public interface ChannelDescriptionEditEventHandler {
    void handleEvent(ChannelDescriptionEditedEvent event);
}
