package net.scrumplex.sprummlbot.plugins.events;

import com.github.theholywaffle.teamspeak3.api.event.ServerEditedEvent;

public interface ServerEditEventHandler {
    void handleEvent(ServerEditedEvent event);
}
