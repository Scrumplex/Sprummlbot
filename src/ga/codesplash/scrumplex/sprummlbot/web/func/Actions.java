package ga.codesplash.scrumplex.sprummlbot.web.func;

import com.github.theholywaffle.teamspeak3.commands.CBanClient;

import ga.codesplash.scrumplex.sprummlbot.Config;
import ga.codesplash.scrumplex.sprummlbot.WebGUILogins;

public class Actions {

	public static String shutdown() {
		System.exit(0);
		return "Shutting down";
	}

	public static String kick(int cid, String msg) {
		if (Config.API.kickClientFromServer(msg, cid)) {
			return new ga.codesplash.scrumplex.sprummlbot.web.manage.Site_kick(true).content;
		}
		return new ga.codesplash.scrumplex.sprummlbot.web.manage.Site_kick(false).content;
	}

	public static String ban(int cid, String msg, int time) {
		final CBanClient client = new CBanClient(cid, time, msg);
		if (Config.QUERY.doCommand(client)) {
			return new ga.codesplash.scrumplex.sprummlbot.web.manage.Site_ban(true).content;
		}
		return new ga.codesplash.scrumplex.sprummlbot.web.manage.Site_ban(false).content;
	}

	public static String unban(int id) {
		return new ga.codesplash.scrumplex.sprummlbot.web.manage.Site_unban(Config.API.deleteBan(id)).content;
	}

	public static String clearAccounts() {
		WebGUILogins.AVAILABLE.clear();
		return new ga.codesplash.scrumplex.sprummlbot.web.manage.Site_clearaccounts().content;
	}

	public static String poke(int userid, String msg) {
		Config.API.pokeClient(userid, msg);
		return new ga.codesplash.scrumplex.sprummlbot.web.Site_index().content;
	}
	
	public static String sendpriv(int userid, String msg) {
		Config.API.sendPrivateMessage(userid, msg);
		return new ga.codesplash.scrumplex.sprummlbot.web.Site_index().content;
	}
}
