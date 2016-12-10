package net.scrumplex.sprummlbot.wrapper;

import net.scrumplex.sprummlbot.Sprummlbot;
import net.scrumplex.sprummlbot.tools.Exceptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionGroup {

    private final String name;
    private final List<String> clients = new ArrayList<>();
    private final List<Integer> groups = new ArrayList<>();
    private final List<String> includes = new ArrayList<>();
    private final Map<String, Permission> clientCache = new HashMap<>();

    private static final Map<String, PermissionGroup> permissionGroups = new HashMap<>();
    private static final Map<String, String> permissionGroupFields = new HashMap<>();

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

    @Deprecated
    public boolean isClientInGroup(String uid) {
        return isPermitted(uid) == Permission.PERMITTED;
    }

    /**
     * This method checks if the given client's uid is in this permission group's instance.
     * <br>
     * <b>NOTE: This function uses cached information if available.
     * If you want to fetch data from server use {@link #isPermitted(String, boolean)} with 2nd parameter set to false.
     * Use {@link #purgeCache()} regularly!</b>
     *
     * @param uid UID of the client, that has to be checked if permitted
     * @return if client is permitted or not. Returns {@link Permission#ERROR}, if a network error occurred.
     */
    public Permission isPermitted(String uid) {
        return isPermitted(uid, true);
    }


    /**
     * This method checks if the given client's uid is in this permission group's instance.
     *
     * @param uid UID of the client, that has to be checked if permitted
     * @return if client is permitted or not. Returns {@link Permission#ERROR}, if a network error occurred.
     */
    public Permission isPermitted(String uid, boolean enableCache) {
        if (uid == null)
            return Permission.DENIED;
        if (name.equalsIgnoreCase(".")) {
            return Permission.DENIED;
        } else if (name.equalsIgnoreCase("*")) {
            return Permission.PERMITTED;
        }

        if (enableCache) {
            if (clientCache.containsKey(uid))
                return clientCache.get(uid);
        }

        try {
            if (clients.contains(uid)) {
                cacheClient(uid, Permission.PERMITTED);
                return Permission.PERMITTED;
            }
            for (String group : includes) {
                if (getPermissionGroupByName(group).isPermitted(uid) == Permission.PERMITTED) {
                    cacheClient(uid, Permission.PERMITTED);
                    return Permission.PERMITTED;
                }
            }
            for (int group : Sprummlbot.getSprummlbot().getSyncAPI().getClientByUId(uid).getServerGroups())
                if (groups.contains(group)) {
                    cacheClient(uid, Permission.PERMITTED);
                    return Permission.PERMITTED;
                }
        } catch (Exception ex) {
            Exceptions.handle(ex, "Couldn't fetch permission group info for client with uid " + uid + "!", false);
            return Permission.ERROR;
        }
        cacheClient(uid, Permission.DENIED);
        return Permission.DENIED;
    }

    public void purgeCache() {
        clientCache.clear();
    }

    private void cacheClient(String uid, Permission permission) {
        clientCache.put(uid, permission);
    }

    public String getName() {
        return name;
    }

    public static void addPermissionGroup(PermissionGroup group) {
        permissionGroups.put(group.getName(), group);
    }

    public static List<PermissionGroup> getPermissionGroups() {
        return new ArrayList<>(permissionGroups.values());
    }

    public static PermissionGroup getPermissionGroupByName(String groupName) {
        if (groupName.equals(".") || groupName.equals("*"))
            return new PermissionGroup(groupName);
        return permissionGroups.get(groupName);
    }

    /**
     * <b>Internal</b> ignore this
     *
     * @param field     Field
     * @param groupName Group's name
     */
    public static void setPermissionGroupField(String field, String groupName) {
        permissionGroupFields.put(field, groupName);
    }

    /**
     * <b>Internal</b> ignore this
     *
     * @param field Field
     * @return Group assigned to field
     */
    public static PermissionGroup getPermissionGroupForField(String field) {
        return getPermissionGroupByName(permissionGroupFields.get(field));
    }

    public enum Permission {
        PERMITTED,
        DENIED,
        ERROR
    }
}
