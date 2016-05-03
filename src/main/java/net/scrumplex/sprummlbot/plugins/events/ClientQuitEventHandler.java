package net.scrumplex.sprummlbot.plugins.events;

import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;

public interface ClientQuitEventHandler {
    public void handleEvent(ClientLeaveEvent event);
}
