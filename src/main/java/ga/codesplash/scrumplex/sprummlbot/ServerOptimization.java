package ga.codesplash.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.api.ServerInstanceProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * This class changes some server settings, to optimize the Sprummlbot
 */
class ServerOptimization {

    /**
     * Sets up all needed permissions for the bot.
     */
    public static void permissions() {
        Map<String, Integer> perms = new HashMap<>();
        perms.put("i_client_move_power", 2000);
        perms.put("i_client_kick_from_server_power", 2000);
        perms.put("i_client_ban_power", 2000);
        perms.put("i_client_ban_max_bantime", -1);
        perms.put("b_client_ban_create", 1);
        perms.put("b_client_ban_list", 1);
        perms.put("i_channel_permission_modify_power", 2000);
        perms.put("i_group_member_add_power", 2000);
        perms.put("i_group_member_remove_power", 2000);
        perms.put("i_group_modify_power", 2000);
        for (String str : perms.keySet()) {
            Vars.API.addServerGroupPermission(2, str, perms.get(str), false, false);
        }
    }

    /**
     * Changes the flood limits
     */
    public static void changeFloodLimits() {
        Vars.API.editInstance(ServerInstanceProperty.SERVERINSTANCE_SERVERQUERY_FLOOD_COMMANDS, "60");
        Vars.API.editInstance(ServerInstanceProperty.SERVERINSTANCE_SERVERQUERY_FLOOD_TIME, "15");
        Vars.API.editInstance(ServerInstanceProperty.SERVERINSTANCE_SERVERQUERY_BAN_TIME, "600");
    }

}
