package ga.scrumplex.ml.sprum.sprummlbot.stuff;

import com.github.theholywaffle.teamspeak3.TS3Api;

import ga.scrumplex.ml.sprum.sprummlbot.Config;

public class PermissionModifier {

	public static void allow() {
		TS3Api api = Config.API;
		api.addServerGroupPermission(2, "i_client_move_power", 2000, false, false);
		api.addServerGroupPermission(2, "i_client_kick_from_server_power", 2000, false, false);
		api.addServerGroupPermission(2, "i_client_ban_power", 2000, false, false);
		api.addServerGroupPermission(2, "i_client_ban_max_bantime", -1, false, false);
		api.addServerGroupPermission(2, "b_client_ban_create", 1, false, false);
		api.addServerGroupPermission(2, "b_client_ban_list", 1, false, false);
	}

}
