package ga.scrumplex.ml.sprum.sprummlbot.web.func;

import com.github.theholywaffle.teamspeak3.commands.CBanClient;

import ga.scrumplex.ml.sprum.sprummlbot.Config;
import ga.scrumplex.ml.sprum.sprummlbot.WebGUILogins;

public class action {

	public static String listclients() {
	    return new ga.scrumplex.ml.sprum.sprummlbot.web.manage.listclients().content;
	}
	
	public static String shutdown() {
        System.exit(0);
        return "Shutting down";
	}
	
	public static String sendServerMSG(String msg) {
	    Config.api.sendServerMessage(msg);
	    return new ga.scrumplex.ml.sprum.sprummlbot.web.manage.sendmessage().content;
	}	
	public static String sendPrivateMSG(String msg, int userid) {
	    Config.api.sendPrivateMessage(userid, msg);
	    return new ga.scrumplex.ml.sprum.sprummlbot.web.manage.sendprivmessage().content;
	}
	
	public static String kick(int cid, String msg) {
		if(Config.api.kickClientFromServer(msg, cid)) {
			return new ga.scrumplex.ml.sprum.sprummlbot.web.manage.kick(true).content;
		}
	    return new ga.scrumplex.ml.sprum.sprummlbot.web.manage.kick(false).content;
	}
	
	public static String ban(int cid, String msg, int time) {
		final CBanClient client = new CBanClient(cid, time, msg);
		if (Config.query.doCommand(client)) {
			return new ga.scrumplex.ml.sprum.sprummlbot.web.manage.ban(true).content;
		}
		return new ga.scrumplex.ml.sprum.sprummlbot.web.manage.ban(false).content;
	}
	
	public static String clearaccounts() {
		WebGUILogins.available.clear();
		return new ga.scrumplex.ml.sprum.sprummlbot.web.manage.clearaccounts().content;
	}
}
