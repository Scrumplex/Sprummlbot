package net.scrumplex.sprummlbot.plugins.events;

import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;

public interface ClientMoveEventHandler {
    void handleEvent(ClientMovedEvent event);
}
