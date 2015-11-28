package ga.codesplash.scrumplex.sprummlbot.web.func;

import com.github.theholywaffle.teamspeak3.commands.CBanClient;

import ga.codesplash.scrumplex.sprummlbot.Vars;

public class Actions {

	public static String shutdown() {
		System.exit(0);
		return "Shutting down";
	}

	public static String kick(int cid, String msg) {
		if (Vars.API.kickClientFromServer(msg, cid)) {
			return new ga.codesplash.scrumplex.sprummlbot.web.manage.Site_kick(true).content;
		}
		return new ga.codesplash.scrumplex.sprummlbot.web.manage.Site_kick(false).content;
	}

	public static String ban(int cid, String msg, int time) {
		final CBanClient client = new CBanClient(cid, time, msg);
		if (Vars.QUERY.doCommand(client)) {
			return new ga.codesplash.scrumplex.sprummlbot.web.manage.Site_ban(true).content;
		}
		return new ga.codesplash.scrumplex.sprummlbot.web.manage.Site_ban(false).content;
	}

	public static String unban(int id) {
		return new ga.codesplash.scrumplex.sprummlbot.web.manage.Site_unban(Vars.API.deleteBan(id)).content;
	}

	public static String clearAccounts() {
		Vars.AVAILABLE_LOGINS.clear();
		return new ga.codesplash.scrumplex.sprummlbot.web.manage.Site_clearaccounts().content;
	}

	public static String poke(int userid, String msg) {
		Vars.API.pokeClient(userid, msg);
		return new ga.codesplash.scrumplex.sprummlbot.web.Site_index().content;
	}
	
	public static String sendpriv(int userid, String msg) {
		Vars.API.sendPrivateMessage(userid, msg);
		return new ga.codesplash.scrumplex.sprummlbot.web.Site_index().content;
	}
}
