package net.scrumplex.sprummlbot.plugins.events;

import com.github.theholywaffle.teamspeak3.api.event.ServerEditedEvent;

public interface ServerEditEventHandler {
    public void handleEvent(ServerEditedEvent event);
}
