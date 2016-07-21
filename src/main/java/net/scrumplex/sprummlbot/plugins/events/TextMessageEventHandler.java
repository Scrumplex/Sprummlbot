package net.scrumplex.sprummlbot.plugins.events;

import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;

public interface TextMessageEventHandler {
    void handleEvent(TextMessageEvent event);
}
