package net.scrumplex.sprummlbot.tools;

import net.scrumplex.sprummlbot.Vars;

import java.util.ArrayList;
import java.util.List;

public class PermissionGroup {

    private final String name;
    private List<String> clients = new ArrayList<>();
    private List<Integer> groups = new ArrayList<>();
    private List<String> includes = new ArrayList<>();

    public PermissionGroup(String name) {
        this.name = name;
    }

    public void addClient(String uid) {
        clients.add(uid);
    }

    public void removeClient(String uid) {
        clients.remove(uid);
    }

    public void addGroup(int groupId) {
        groups.add(groupId);
    }

    public void addIncludingPermissionGroup(String name) {
        includes.add(name);
    }

    public boolean isClientInGroup(String uid) {
        try {
            if (clients.contains(uid))
                return true;
            for (int group : Vars.API.getClientByUId(uid).get().getServerGroups())
                if (groups.contains(group))
                    return true;
            for (String group : includes) {
                if (Vars.PERMGROUPS.get(group).isClientInGroup(uid))
                    return true;
            }
        } catch (InterruptedException ex) {
            Exceptions.handle(ex, "Couldn't get Server Groups.");
        }
        return false;
    }

    public String getName() {
        return name;
    }

}
