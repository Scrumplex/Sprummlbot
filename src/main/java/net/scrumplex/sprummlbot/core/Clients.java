package net.scrumplex.sprummlbot.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Clients {

    private final Map<Integer, ClientFlags> clients = new HashMap<>();

    public void updateClientFlags(int clientId, ClientFlags flag) {
        clients.put(clientId, flag);
    }

    public ClientFlags getClientFlags(int clientId) {
        if (!clients.containsKey(clientId)) {
            ClientFlags clientFlags = new ClientFlags();
            clients.put(clientId, clientFlags);
        }

        return clients.get(clientId);
    }

    public void removeClientFlags(int clientId) {
        clients.remove(clientId);
    }


    public class ClientFlags {
        private final Map<String, Object> clientFlags = new HashMap<>();

        public void addClientFlag(String clientFlag) {
            clientFlags.put(clientFlag, null);
        }

        public void addClientFlag(String clientFlag, Object information) {
            clientFlags.put(clientFlag, information);
        }

        public void addClientFlag(DefaultClientFlags clientFlag) {
            clientFlags.put(clientFlag.toString(), null);
        }

        public void addClientFlag(DefaultClientFlags clientFlag, Object information) {
            clientFlags.put(clientFlag.toString(), information);
        }

        public void removeClientFlag(String clientFlag) {
            clientFlags.remove(clientFlag);
        }

        public void removeClientFlag(DefaultClientFlags clientFlag) {
            clientFlags.remove(clientFlag.toString());
        }

        public boolean hasFlag(String clientFlag) {
            return clientFlags.containsKey(clientFlag);
        }

        public boolean hasFlag(DefaultClientFlags clientFlag) {

            return clientFlags.containsKey(clientFlag.toString());
        }

        public Object getInformation(String clientFlag) {
            return clientFlags.get(clientFlag);
        }

        public Object getInformation(DefaultClientFlags clientFlag) {
            return clientFlags.get(clientFlag.toString());
        }
    }

    public enum DefaultClientFlags implements Serializable {
        AFK("afk"),
        NOTIFY("notify");

        private final String flagName;

        DefaultClientFlags(String flagName) {
            this.flagName = flagName;
        }

        @Override
        public String toString() {
            return flagName;
        }
    }
}
