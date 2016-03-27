package net.scrumplex.sprummlbot.plugins;

import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import net.scrumplex.sprummlbot.wrapper.CommandResponse;

public interface CommandHandler {

    CommandResponse handleCommand(ClientInfo invoker, String[] args) throws Exception;

}
