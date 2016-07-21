package net.scrumplex.sprummlbot.plugins.events;

import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;

public interface ClientQuitEventHandler {
    void handleEvent(ClientLeaveEvent event);
}
