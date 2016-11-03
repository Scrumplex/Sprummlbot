package net.scrumplex.sprummlbot.plugins;

import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import net.scrumplex.sprummlbot.wrapper.CommandResponse;

public interface CommandHandler {

    /**
     * This method is being executed, if a client requests a command assigned to this CommandHandler.
     *
     * @param invoker The client, who invoked the command.
     * @param args    A String array, containing all arguments passed by the invoker. Empty, if no arguments passed.
     * @return a CommandResponse
     * @throws Exception catches all exceptions and informs invoker if an exception is thrown.
     * @see com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo
     * @see net.scrumplex.sprummlbot.wrapper.CommandResponse;
     */
    CommandResponse handleCommand(ClientInfo invoker, String[] args) throws Exception;
}
