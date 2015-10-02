package ga.scrumplex.ml.sprum.sprummlbot.web.func;

import com.github.theholywaffle.teamspeak3.commands.CBanClient;

import ga.scrumplex.ml.sprum.sprummlbot.Config;
import ga.scrumplex.ml.sprum.sprummlbot.WebGUILogins;

public class Actions {
	
	public static String shutdown() {
        System.exit(0);
        return "Shutting down";
	}
	
	public static String kick(int cid, String msg) {
		if(Config.API.kickClientFromServer(msg, cid)) {
			return new ga.scrumplex.ml.sprum.sprummlbot.web.manage.Site_kick(true).content;
		}
	    return new ga.scrumplex.ml.sprum.sprummlbot.web.manage.Site_kick(false).content;
	}
	
	public static String ban(int cid, String msg, int time) {
		final CBanClient client = new CBanClient(cid, time, msg);
		if (Config.QUERY.doCommand(client)) {
			return new ga.scrumplex.ml.sprum.sprummlbot.web.manage.Site_ban(true).content;
		}
		return new ga.scrumplex.ml.sprum.sprummlbot.web.manage.Site_ban(false).content;
	}
	
	public static String unban(int id) {
		return new ga.scrumplex.ml.sprum.sprummlbot.web.manage.Site_unban(Config.API.deleteBan(id)).content;
	}
	
	public static String clearAccounts() {
		WebGUILogins.AVAILABLE.clear();
		return new ga.scrumplex.ml.sprum.sprummlbot.web.manage.Site_clearaccounts().content;
	}
}
