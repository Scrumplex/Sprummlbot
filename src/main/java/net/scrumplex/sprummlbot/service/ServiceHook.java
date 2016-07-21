package net.scrumplex.sprummlbot.service;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import java.util.List;

public interface ServiceHook {

    void handle(List<Client> clientInfo);

}
