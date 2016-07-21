package net.scrumplex.sprummlbot.module;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import net.scrumplex.sprummlbot.Sprummlbot;
import net.scrumplex.sprummlbot.service.ServiceHook;
import org.ini4j.Profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerGroupProtector extends Module {

    private int groupId;
    private List<String> clients;

    @Override
    protected void load(Profile.Section sec) throws Exception {
        groupId = sec.get("servergroup_id", int.class);
        clients = getClients(sec);
    }

    @Override
    protected void start() {
        getMainService().hook(this, new Hook(groupId, clients, Sprummlbot.getSprummlbot().getSyncAPI()));
    }

    @Override
    protected void stop() {
        getMainService().unhook(this);
    }

    private List<String> getClients(Profile.Section sec) {
        List<String> clients = new ArrayList<>();
        for (String line : sec.getAll("client")) {
            if (line.contains(" ")) {
                line = line.replaceAll("\\s+", "");
            }
            if (line.contains(",")) {
                Collections.addAll(clients, line.split(","));
            } else {
                clients.add(line);
            }
        }
        return clients;
    }

    private class Hook implements ServiceHook {

        private final int groupId;
        private final List<String> uids;
        private final TS3Api api;

        Hook(int groupId, List<String> uids, TS3Api api) {
            this.groupId = groupId;
            this.uids = uids;
            this.api = api;
        }

        @Override
        public void handle(List<Client> clients) {
            for (Client c : clients) {
                if (uids.contains(c.getUniqueIdentifier()) && !c.isInServerGroup(groupId)) {
                    api.addClientToServerGroup(groupId, c.getDatabaseId());
                    System.out.println("[Server Group Protector] Added " + c.getNickname() + "(" + c.getId() + ") to server group " + groupId);
                } else if (!uids.contains(c.getUniqueIdentifier()) && c.isInServerGroup(groupId)) {
                    api.removeClientFromServerGroup(groupId, c.getDatabaseId());
                    System.out.println("[Server Group Protector] Removed " + c.getNickname() + "(" + c.getId() + ") from server group " + groupId);
                }
            }
        }
    }
}
