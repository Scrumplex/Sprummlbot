package net.scrumplex.sprummlbot.plugins.events;

import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;

public interface ClientJoinEventHandler {
    public void handleEvent(ClientJoinEvent event);
}
