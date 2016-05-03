package net.scrumplex.sprummlbot.plugins.events;

import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;

public interface TextMessageEventHandler {
    public void handleEvent(TextMessageEvent event);
}
