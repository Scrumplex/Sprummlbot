package ga.codesplash.scrumplex.sprummlbot.stuff;

import ga.codesplash.scrumplex.sprummlbot.Config;

public class ServerOptimization {

	public static void permissions() {
		Config.API.addServerGroupPermission(2, "i_client_move_power", 2000, false, false);
		Config.API.addServerGroupPermission(2, "i_client_kick_from_server_power", 2000, false, false);
		Config.API.addServerGroupPermission(2, "i_client_ban_power", 2000, false, false);
		Config.API.addServerGroupPermission(2, "i_client_ban_max_bantime", -1, false, false);
		Config.API.addServerGroupPermission(2, "b_client_ban_create", 1, false, false);
		Config.API.addServerGroupPermission(2, "b_client_ban_list", 1, false, false);
	}

}
