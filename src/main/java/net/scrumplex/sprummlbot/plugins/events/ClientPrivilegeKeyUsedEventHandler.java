package net.scrumplex.sprummlbot.plugins.events;

import com.github.theholywaffle.teamspeak3.api.event.PrivilegeKeyUsedEvent;

public interface ClientPrivilegeKeyUsedEventHandler {
    public void handleEvent(PrivilegeKeyUsedEvent event);
}
